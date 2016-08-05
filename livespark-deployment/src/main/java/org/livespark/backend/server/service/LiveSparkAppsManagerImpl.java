/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.livespark.backend.server.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppsManager;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.walkFileTree;

@Service
@ApplicationScoped
public class LiveSparkAppsManagerImpl implements LiveSparkAppsManager {

    protected Gson gson;
    private IOService ioService;
    private FileSystem fileSystem;

    private Path root;


    @Inject
    public LiveSparkAppsManagerImpl( @Named( "ioStrategy" ) IOService ioService ) {
        this.ioService = ioService;
    }

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            fileSystem = ioService.newFileSystem( URI.create( "default://livespark" ),
                                                  new HashMap<String, Object>() {{
                                                      put( "init", Boolean.TRUE );
                                                      put( "internal", Boolean.TRUE );
                                                  }} );
        } catch ( FileSystemAlreadyExistsException e ) {
            fileSystem = ioService.getFileSystem( URI.create( "default://livespark" ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    @Override
    public List<LiveSparkApp> getRegisteredApps() {

        final List<LiveSparkApp> result = new ArrayList<LiveSparkApp>();

        if ( ioService.exists( root ) ) {
            walkFileTree( checkNotNull( "path", root ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );

                                      if ( attrs.isRegularFile() ) {
                                          LiveSparkApp app = buildLiveSparkApp( file );

                                          if ( app != null ) {
                                              result.add( app );
                                          }
                                      }
                                  } catch ( final Exception ex ) {
                                      //logger.error( "An unexpected exception was thrown: ", ex );
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );
        }

        return result;
    }

    private LiveSparkApp buildLiveSparkApp( final Path appPath ) {

        if ( appPath.getFileName().toString().endsWith( ".ls" ) ) {

            return gson.fromJson( ioService.readAllString( appPath ), LiveSparkApp.class );

        }

        return null;
    }

    @Override
    public void registerApp( LiveSparkApp liveSparkApp ) {
        checkNotNull( "App cannot be null", liveSparkApp );

        final Path appPath = getLiveSparkAppPath( liveSparkApp );
        if ( ioService.exists( appPath ) ) {
            return;
        }

        try {
            ioService.startBatch( fileSystem );
            ioService.write( appPath,
                             gson.toJson( liveSparkApp ) );
        } finally {
            ioService.endBatch();
        }
    }

    private Path getLiveSparkAppPath( final LiveSparkApp liveSparkApp ) {
        return root.resolve( liveSparkApp.getGAV() + ".ls" );
    }
}
