/*
 * Copyright 2016 JBoss Inc
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
package org.livespark.client;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.search.client.menu.SearchMenuBuilder;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.authz.PermissionTreeSetup;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.livespark.client.home.HomeProducer;
import org.livespark.client.resources.i18n.AppConstants;
import org.livespark.deployment.model.LiveSparkApp;
import org.livespark.deployment.service.LiveSparkAppsManager;
import org.livespark.deployment.service.events.AppReady;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

@EntryPoint
public class LiveSparkEntryPoint extends DefaultWorkbenchEntryPoint {

    protected AppConstants constants = AppConstants.INSTANCE;

    protected HomeProducer homeProducer;

    protected Caller<SocialConfigurationService> socialConfigurationService;

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected ClientUserSystemManager userSystemManager;

    protected WorkbenchMenuBarPresenter menuBar;

    protected SyncBeanManager iocManager;

    protected Workbench workbench;

    protected PlaceManager placeManager;

    protected PermissionTreeSetup permissionTreeSetup;

    @Inject
    protected Caller<LiveSparkAppsManager> liveSparkAppsManager;

    @Inject
    public LiveSparkEntryPoint( Caller<AppConfigService> appConfigService,
                                Caller<PlaceManagerActivityService> pmas,
                                ActivityBeansCache activityBeansCache,
                                HomeProducer homeProducer,
                                Caller<SocialConfigurationService> socialConfigurationService,
                                DefaultWorkbenchFeaturesMenusHelper menusHelper,
                                ClientUserSystemManager userSystemManager,
                                WorkbenchMenuBarPresenter menuBar,
                                SyncBeanManager iocManager,
                                Workbench workbench,
                                PlaceManager placeManager,
                                PermissionTreeSetup permissionTreeSetup ) {
        super( appConfigService, pmas, activityBeansCache );
        this.homeProducer = homeProducer;
        this.socialConfigurationService = socialConfigurationService;
        this.menusHelper = menusHelper;
        this.userSystemManager = userSystemManager;
        this.menuBar = menuBar;
        this.iocManager = iocManager;
        this.workbench = workbench;
        this.placeManager = placeManager;
        this.permissionTreeSetup = permissionTreeSetup;
    }

    @PostConstruct
    public void init() {
        workbench.addStartupBlocker( LiveSparkEntryPoint.class );
        homeProducer.init();
        permissionTreeSetup.configureTree();
    }

    protected void onAppReady( @Observes AppReady appReady ) {
        refreshMenus();

        goToLiveSparkAppScreen( appReady.getApp() );
    }

    @Override
    protected void setupMenu() {

        // Social services.
        socialConfigurationService.call( new RemoteCallback<Boolean>() {
            public void callback( final Boolean socialEnabled ) {

                liveSparkAppsManager.call( new RemoteCallback<List<LiveSparkApp>>() {

                    @Override
                    public void callback( final List<LiveSparkApp> deployedApps ) {
                        // Wait for user management services to be initialized, if any.
                        userSystemManager.waitForInitialization( () -> {

                            menuBar.clear();
                            final Menus menus;
                            menus = MenuFactory.newTopLevelMenu( constants.home() ).withItems( menusHelper.getHomeViews(
                                    socialEnabled ) ).endMenu()
                                    .newTopLevelMenu( constants.authoring() ).withItems( menusHelper.getAuthoringViews() ).endMenu()
                                    .newTopLevelMenu( constants.deploy() ).withItems( getDeploymentViews() ).endMenu()
                                    .newTopLevelMenu( constants.extensions() ).withItems( getExtensionsViews() ).endMenu()
                                    .newTopLevelCustomMenu( iocManager.lookupBean( SearchMenuBuilder.class ).getInstance() ).endMenu()
                                    .newTopLevelMenu( constants.deployedApps() ).withItems( getLiveSparkDeployedApps(
                                            deployedApps ) ).endMenu()
                                    .build();

                            menuBar.addMenus( menus );

                            menusHelper.addRolesMenuItems();
                            menusHelper.addWorkbenchConfigurationMenuItem();
                            menusHelper.addUtilitiesMenuItems();

                            workbench.removeStartupBlocker( LiveSparkEntryPoint.class );
                        } );
                    }
                } ).getRegisteredApps();

            }
        } ).isSocialEnable();
    }

    private List<? extends MenuItem> getLiveSparkDeployedApps( List<LiveSparkApp> deployedApps ) {
        final List<MenuItem> result = new ArrayList<>( 2 );

        for ( LiveSparkApp app : deployedApps ) {
            result.add( newAppMenuItem( app ) );
        }

        result.add( MenuFactory.newSimpleItem( constants.deployedAppsAdministration() ).respondsWith( () -> placeManager.goTo(
                new DefaultPlaceRequest( "LiveSparkAppsEditionPerspective" ) ) ).endMenu().build().getItems().get( 0 ) );
        return result;
    }

    private MenuItem newAppMenuItem( final LiveSparkApp app ) {

        return MenuFactory.newSimpleItem( app.toString() )
                .respondsWith( () -> {
                    goToLiveSparkAppScreen( app );
                } ).endMenu().build().getItems().get( 0 );
    }

    protected void goToLiveSparkAppScreen( LiveSparkApp app ) {
        Assert.notNull( "App cannot be null", app );
        PlaceRequest request = new DefaultPlaceRequest( "LiveSparkAppPerspective" );
        request.addParameter( "appId", app.getId() );
        request.addParameter( "appName", app.toString() );
        placeManager.goTo( request );
    }

    private void refreshMenus() {

        setupMenu();
    }


    protected List<? extends MenuItem> getExtensionsViews() {

        //the data source management perspective is not yet added to the platform by default extensions
        //to be sure we don't interfere in other workbenches. So it's basically added here.
        final List<MenuItem> result = new ArrayList<>();

        result.addAll( menusHelper.getExtensionsViews() );

        result.add( MenuFactory.newSimpleItem( constants.dataSourceManagement() )
                            .place( new DefaultPlaceRequest( DATASOURCE_MANAGEMENT ) )
                            .endMenu()
                            .build()
                            .getItems()
                            .get( 0 ) );

        return result;
    }

    protected List<MenuItem> getDeploymentViews() {
        final List<MenuItem> result = new ArrayList<>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.ruleDeployments() )
                            .perspective( SERVER_MANAGEMENT )
                            .endMenu().build().getItems().get( 0 ) );

        return result;
    }
}
