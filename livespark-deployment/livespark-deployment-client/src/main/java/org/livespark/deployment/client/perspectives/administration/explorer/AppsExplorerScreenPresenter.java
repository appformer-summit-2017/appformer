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

package org.livespark.deployment.client.perspectives.administration.explorer;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.livespark.deployment.client.resources.i18n.DeploymentConstants;
import org.livespark.deployment.model.LiveSparkApp;
import org.livespark.deployment.service.LiveSparkAppsManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen( identifier = "AppsExplorerScreenPresenter" )
public class AppsExplorerScreenPresenter {

    protected AppsExplorerScreenView view;
    protected Caller<LiveSparkAppsManager> liveSparkAppsManager;
    protected TranslationService translationService;
    protected ManagedInstance<AppItemPresenter> appItemPresenters;
    protected PlaceManager placeManager;

    @Inject
    public AppsExplorerScreenPresenter( AppsExplorerScreenView view,
                                        Caller<LiveSparkAppsManager> liveSparkAppsManager,
                                        TranslationService translationService,
                                        ManagedInstance<AppItemPresenter> appItemPresenters,
                                        PlaceManager placeManager ) {
        this.view = view;
        this.liveSparkAppsManager = liveSparkAppsManager;
        this.translationService = translationService;
        this.appItemPresenters = appItemPresenters;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @OnStartup
    public void onStartup( PlaceRequest placeRequest ) {
        view.clear();
        liveSparkAppsManager.call( new RemoteCallback<List<LiveSparkApp>>() {
            @Override
            public void callback( List<LiveSparkApp> liveSparkApps ) {
                if ( liveSparkApps != null ) {
                    for (LiveSparkApp liveSparkApp : liveSparkApps ) {
                        AppItemPresenter appItemPresenter = appItemPresenters.get();

                        appItemPresenter.init( liveSparkApp, () -> administrateApp( liveSparkApp ) );
                        view.showAppPresenter( appItemPresenter );
                    }
                }
            }
        } ).getRegisteredApps();
    }

    protected void administrateApp( LiveSparkApp app ) {
        Assert.notNull( "App cannot be null", app );
        PlaceRequest request = new DefaultPlaceRequest( "AppsEditorScreenPresenter" );
        request.addParameter( "appId", app.getId() );
        placeManager.goTo( request );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation( DeploymentConstants.deployedApps );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view.asWidget();
    }

    public interface AppsExplorerScreenView extends IsWidget {
        void init( AppsExplorerScreenPresenter presenter );

        void showAppPresenter( AppItemPresenter appItemPresenter );

        void clear();
    }
}
