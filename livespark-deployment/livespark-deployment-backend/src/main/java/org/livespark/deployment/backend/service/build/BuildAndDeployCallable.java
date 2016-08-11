/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.livespark.deployment.backend.service.build;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.maven.shared.invoker.InvocationResult;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.api.ServerMessageBus;
import org.livespark.deployment.model.LiveSparkApp;
import org.livespark.deployment.service.LiveSparkAppBuilder;

public class BuildAndDeployCallable extends BaseBuildCallable implements HttpSessionBindingListener {

    protected final HttpSession session;
    private final Set<File> deployedWars = new LinkedHashSet<>();

    BuildAndDeployCallable( Project project,
                            File pomXml,
                            HttpSession session,
                            String queueSessionId,
                            ServletRequest sreq,
                            ServerMessageBus bus,
                            LiveSparkAppBuilder liveSparkAppBuilder ) {
        super( project, pomXml, queueSessionId, sreq, bus, liveSparkAppBuilder );
        this.session = session;
    }

    @Override
    protected List<BuildMessage> postBuildTasks( InvocationResult res ) throws Exception {
        final List<BuildMessage> messages = new ArrayList<BuildMessage>();
        messages.addAll( processBuildResults( res ) );
        messages.addAll( deployIfSuccessful( res ) );

        return messages;
    }

    protected List<BuildMessage> deployIfSuccessful( InvocationResult res ) throws MalformedURLException, URISyntaxException, IOException, Exception {
        if ( res.getExitCode() == 0 )
            deploy();

        return Collections.emptyList();
    }

    protected void deploy() throws MalformedURLException, URISyntaxException, IOException, Exception {
        final Collection<File> wars = getTargetWarFiles();
        File deployDir = getDeployDir();
        for ( final File war : wars ) {
            final File destination = getDeployWarFile( deployDir, war.getName() );

            sendOutputToClient( "Deploying " + war.getName() + " as " + destination.getName() + " ..." );
            replaceDeployedWarFile( war, destination );
            maybeStartDeployedFileMonitor( deployDir, destination );
        }
    }

    private void maybeStartDeployedFileMonitor( File deployDir, final File destination ) throws Exception {
        final String monitorHandleAttr = getFileMonitorHandleAttributeName( destination );
        FileMonitorHandle monitorHandle = (FileMonitorHandle) session.getAttribute( monitorHandleAttr );

        if ( monitorHandle == null ) {
            monitorHandle = startDeployedFileObserver( deployDir, destination );
            session.setAttribute( monitorHandleAttr, monitorHandle );
        }
    }

    private String getFileMonitorHandleAttributeName( final File destination ) {
        return FileMonitorHandle.class.getSimpleName() + "-" + destination.getName();
    }

    private FileMonitorHandle startDeployedFileObserver( File deployDir, final File destination ) throws Exception {
        final FileAlterationMonitor monitor = new FileAlterationMonitor( 500 );
        final IOFileFilter filter = FileFilterUtils.nameFileFilter( destination.getName() + ".deployed" );
        final FileAlterationObserver observer = new FileAlterationObserver( deployDir, filter );
        observer.addListener( new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate( final File file ) {
                buildLiveSparkApp( destination, sreq );
            }

            @Override
            public void onFileChange( final File file ) {
                buildLiveSparkApp( destination, sreq );
            }
        } );
        monitor.addObserver( observer );
        monitor.start();

        return new FileMonitorHandle( monitor, destination.getName() );
    }

    private void replaceDeployedWarFile( final File war,
                                         final File destination ) throws IOException {
        FileUtils.deleteQuietly( destination );
        FileUtils.copyFile( war, destination );
        deployedWars.add( destination );
    }

    private File getDeployWarFile( File deployDir, String packagedWarName ) {
        final String deployedWarName = getDeploymentWarName( packagedWarName );
        final File destination = new File( deployDir, deployedWarName );
        return destination;
    }

    private String getDeploymentWarName( String packagedWarName ) {
        return packagedWarName.replaceAll( "\\.war$", queueSessionId + ".war" );
    }

    private File getTargetDir() {
        final File targetDir = new File( pomXml.getParent(), "target" );
        return targetDir;
    }

    private Collection<File> getTargetWarFiles() {
        final File targetDir = getTargetDir();
        return getWarFilesInDir( targetDir );
    }

    private Collection<File> getWarFilesInDir( final File dir ) {
        if ( dir.exists() ) {
            return FileUtils.listFiles( dir, new String[]{"war"}, false );
        } else {
            return Collections.emptyList();
        }
    }

    private File getDeployDir() throws MalformedURLException,
            URISyntaxException {
        String home = getWildflyHome();
        File deployDir = new File( home, "/standalone/deployments" );
        return deployDir;
    }

    private void buildLiveSparkApp( File war, ServletRequest sreq ) {
        final String url = "http://" +
                sreq.getServerName() + ":" +
                sreq.getServerPort() + "/" +
                war.getName().replace( ".war", "" );

        final LiveSparkApp app = new LiveSparkApp( UUID.randomUUID().toString(),
                                                   new Date(),
                                                   project.getProjectName(),
                                                   project.getPom().getGav().toString(),
                                                   project.getPom().getGav().getVersion(),
                                                   url );

        liveSparkAppBuilder.buildAndpublishLiveSparkApp( app, project );
    }

    @Override
    public void valueBound( HttpSessionBindingEvent event ) {
    }

    @Override
    public void valueUnbound( HttpSessionBindingEvent event ) {
        for ( final File war : deployedWars ) {
            FileUtils.deleteQuietly( war );
        }
    }
}