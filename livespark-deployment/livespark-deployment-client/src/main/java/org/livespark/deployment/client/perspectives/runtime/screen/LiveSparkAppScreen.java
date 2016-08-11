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

package org.livespark.deployment.client.perspectives.runtime.screen;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.livespark.deployment.client.perspectives.BaseLiveSparkAppScreen;
import org.livespark.deployment.client.perspectives.util.AppStatusChecker;
import org.livespark.deployment.client.resources.i18n.DeploymentConstants;
import org.livespark.deployment.model.LiveSparkApp;
import org.livespark.deployment.service.LiveSparkAppsManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchScreen( identifier = "LiveSparkAppScreen" )
public class LiveSparkAppScreen extends BaseLiveSparkAppScreen {

    private Panel mainPanel = new FlowPanel();
    private LiveSparkAppScreenView view;

    @Inject
    public LiveSparkAppScreen( LiveSparkAppScreenView view,
                               Caller<LiveSparkAppsManager> liveSparkAppsManager,
                               TranslationService translationService ) {
        super( liveSparkAppsManager, translationService );
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
                                             mainPanel.add( view.asWidget() );
                                             view.init( liveSparkApp );
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
        return translationService.getTranslation( DeploymentConstants.defaultAppTitle );
    }

    public interface LiveSparkAppScreenView extends IsWidget {
        void init( LiveSparkApp liveSparkApp );
    }

}
