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

package org.livespark.client.deployment.perspective.screen;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.jboss.errai.common.client.api.Caller;
import org.livespark.client.deployment.BaseLiveSparkAppScreen;
import org.livespark.client.deployment.util.AppStatusChecker;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppsManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchScreen( identifier = "LiveSparkAppScreen" )
public class LiveSparkAppScreen extends BaseLiveSparkAppScreen {

    public interface LiveSparkAppScreenView extends IsWidget {
        void init( LiveSparkApp liveSparkApp );
    }

    private Panel mainPanel = new FlowPanel();

    private LiveSparkAppScreenView view;

    @Inject
    public LiveSparkAppScreen( LiveSparkAppScreenView view, Caller<LiveSparkAppsManager> liveSparkAppsManager ) {
        super( liveSparkAppsManager );
        this.view = view;
    }

    @OnStartup
    @Override
    public void onStartup( final PlaceRequest place ) {
        super.onStartup( place );
    }

    @Override
    protected void render( LiveSparkApp liveSparkApp ) {
        AppStatusChecker.checkAppStatus( liveSparkApp,
                                         () -> {
                                             view.init( liveSparkApp );
                                             mainPanel.add( view.asWidget() );
                                         },
                                         () -> {
                                             // TODO: show error
                                         } );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return mainPanel;
    }

    @WorkbenchPartTitle
    public String title() {
        if ( appName != null ) {
            return appName;
        }
        return "Your app";
    }

}
