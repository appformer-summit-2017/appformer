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

package org.livespark.deployment.client.perspectives.administration.editor.layout;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.livespark.deployment.client.layoutEditor.LiveSparkAppDragComponent;
import org.livespark.deployment.client.layoutEditor.LiveSparkDisplayerDragComponent;
import org.livespark.deployment.client.resources.i18n.DeploymentConstants;
import org.livespark.deployment.model.LiveSparkApp;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;

@Templated
@Dependent
public class LiveSparkAppLayoutEditorViewImpl extends Composite implements LiveSparkAppLayoutEditor.LiveSparkAppLayoutEditorView {

    protected HTMLLayoutDragComponent htmlLayoutDragComponent;

    protected LiveSparkAppDragComponent liveSparkAppDragComponent;

    protected LiveSparkDisplayerDragComponent liveSparkDisplayerDragComponent;

    protected ManagedInstance<LayoutEditor> layoutEditors;

    protected TranslationService translationService;

    protected LayoutEditor homePageEditor;

    protected LayoutEditor dashboardsEditor;

    @DataField
    protected Panel homePageEditorContent = new FlowPanel();

    @DataField
    protected Panel dashboardsEditorContent = new FlowPanel();

    @Inject
    public LiveSparkAppLayoutEditorViewImpl( HTMLLayoutDragComponent htmlLayoutDragComponent,
                                             LiveSparkAppDragComponent liveSparkAppDragComponent,
                                             LiveSparkDisplayerDragComponent liveSparkDisplayerDragComponent,
                                             ManagedInstance<LayoutEditor> layoutEditors,
                                             TranslationService translationService ) {
        this.htmlLayoutDragComponent = htmlLayoutDragComponent;
        this.liveSparkAppDragComponent = liveSparkAppDragComponent;
        this.liveSparkDisplayerDragComponent = liveSparkDisplayerDragComponent;
        this.layoutEditors = layoutEditors;
        this.translationService = translationService;
    }

    protected void init() {
        homePageEditorContent.clear();
        homePageEditor = layoutEditors.get();
        homePageEditor.addDraggableComponentGroup( getLayoutComponentGroups() );
        homePageEditorContent.add( homePageEditor.asWidget() );
        dashboardsEditorContent.clear();
        dashboardsEditor = layoutEditors.get();
        dashboardsEditor.addDraggableComponentGroup( getLayoutComponentGroups() );
        dashboardsEditorContent.add( dashboardsEditor.asWidget() );
    }

    protected LayoutDragComponentGroup getLayoutComponentGroups() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup( translationService.getTranslation(
                DeploymentConstants.layoutComponents ) );
        group.addLayoutDragComponent( "htmlComponent", htmlLayoutDragComponent );
        group.addLayoutDragComponent( "lsAppComponent", liveSparkAppDragComponent );
        group.addLayoutDragComponent( "lsDisplayerComponent", liveSparkDisplayerDragComponent );
        return group;
    }

    @Override
    public void startEdit( LiveSparkApp liveSparkApp ) {
        liveSparkAppDragComponent.setUp( liveSparkApp );
        init();
        homePageEditor.loadLayout( liveSparkApp.getHome().getTemplate() );
        dashboardsEditor.loadLayout( liveSparkApp.getDashboards().getTemplate() );
    }

    @Override
    public LayoutTemplate getHomePage() {
        return homePageEditor.getLayout();
    }

    @Override
    public LayoutTemplate getDashBoards() {
        return dashboardsEditor.getLayout();
    }
}
