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

package org.livespark.client.deployment.administration.editor.layout;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.livespark.client.layoutEditor.LiveSparkAppDragComponent;
import org.livespark.client.layoutEditor.LiveSparkDisplayerDragComponent;
import org.livespark.client.shared.LiveSparkApp;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;

@Templated
@Dependent
public class LiveSparkAppLayoutEditorViewImpl extends Composite implements LiveSparkAppLayoutEditor.LiveSparkAppLayoutEditorView {

    @Inject
    protected HTMLLayoutDragComponent htmlLayoutDragComponent;

    @Inject
    protected LiveSparkAppDragComponent liveSparkAppDragComponent;

    @Inject
    protected LiveSparkDisplayerDragComponent liveSparkDisplayerDragComponent;

    protected ManagedInstance<LayoutEditor> layoutEditors;

    protected LayoutEditor homePageEditor;

    protected LayoutEditor dashboardsEditor;

    @DataField
    protected SimplePanel homePageEditorContent = new SimplePanel();

    @DataField
    protected SimplePanel dashboardsEditorContent = new SimplePanel();

    @Inject
    public LiveSparkAppLayoutEditorViewImpl( ManagedInstance<LayoutEditor> layoutEditors ) {
        this.layoutEditors = layoutEditors;
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
        LayoutDragComponentGroup group = new LayoutDragComponentGroup( "Components" );
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
