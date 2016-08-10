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

package org.livespark.client.deployment.administration.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.api.Caller;
import org.livespark.client.deployment.BaseLiveSparkAppScreen;
import org.livespark.client.deployment.administration.editor.layout.LiveSparkAppLayoutEditor;
import org.livespark.client.deployment.administration.editor.main.MainPageView;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppsManager;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen( identifier = "AppsEditorScreenPresenter" )
public class AppEditorScreenPresenter extends BaseLiveSparkAppScreen {

    protected FlowPanel content = new FlowPanel();

    protected MainPageView mainPage;

    protected LiveSparkAppLayoutEditor layoutEditor;

    protected Menus menus;

    protected boolean isLayoutEditor = false;

    @Inject
    public AppEditorScreenPresenter( MainPageView mainPage, LiveSparkAppLayoutEditor layoutEditor,
                                     Caller<LiveSparkAppsManager> liveSparkAppsManager ) {
        super( liveSparkAppsManager );
        this.mainPage = mainPage;
        this.layoutEditor = layoutEditor;
    }

    @OnStartup
    @Override
    public void onStartup( final PlaceRequest place ) {
        super.onStartup( place );
        makeMenuBar();
    }

    @Override
    protected void render( LiveSparkApp liveSparkApp ) {
        mainPage.init( liveSparkApp );

        setComponentView( mainPage );
    }

    private void setComponentView( IsWidget widget ) {
        content.clear();
        content.add( widget );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        if ( liveSparkApp != null ) {
            return "Editing " + liveSparkApp.toString();
        }
        return "Editing App";//translationService.getTranslation( DataSourceManagementConstants.DataSourceDefExplorerScreen_Title );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return content;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    protected void makeMenuBar() {
        if ( !isLayoutEditor ) {
            menus = MenuFactory.newTopLevelMenu( "Show App Status" ).respondsWith( new Command() {
                @Override
                public void execute() {
                    showAppStatus();
                }
            } ).endMenu()
                    .newTopLevelMenu( "Edit App Layout" ).respondsWith( new Command() {
                        @Override
                        public void execute() {
                            showLayoutEditor();
                        }
                    } ).endMenu().build();
        }
    }

    protected void showLayoutEditor() {
        layoutEditor.init( liveSparkApp );
        isLayoutEditor = true;
        setComponentView( layoutEditor );
    }

    protected void showAppStatus() {
        layoutEditor.saveLayouts();
        setComponentView( mainPage );
    }

    @OnClose
    public void onClose() {
        if ( isLayoutEditor ) {
            layoutEditor.saveLayouts();
        }
        liveSparkAppsManager.call(  ).updateApp( liveSparkApp );
    }
}
