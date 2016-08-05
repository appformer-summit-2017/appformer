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

package org.livespark.client.dashbuilder;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.editor.DisplayerDragComponent;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.PerspectiveCoordinator;
import org.dashbuilder.displayer.client.widgets.DisplayerEditor;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorPopup;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.livespark.client.shared.LiveSparkApp;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.client.api.HasDragAndDropSettings;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;

@Dependent

public class LiveSparkDisplayerDragComponent extends DisplayerDragComponent implements HasDragAndDropSettings {
    public static final String APP_DATASETS = "appDataSets";

    public static final String separator = ",";

    private String appDataSets = "";

    private final SyncBeanManager beanManager;

    @Inject
    public LiveSparkDisplayerDragComponent( SyncBeanManager beanManager,
                                            DisplayerViewer viewer,
                                            PlaceManager placeManager,
                                            PerspectiveCoordinator perspectiveCoordinator ) {
        super( beanManager, viewer, placeManager, perspectiveCoordinator );
        this.beanManager = beanManager;
    }


    public void setLiveSparkApp( LiveSparkApp liveSparkApp ) {
        appDataSets = separator;
        for ( String dataSet : liveSparkApp.getDataSets() ) {
            appDataSets += dataSet + separator;
        }
    }

    @Override
    public Modal getConfigurationModal( ModalConfigurationContext ctx ) {

        Map<String, String> properties = ctx.getComponentProperties();
        String json = properties.get( "json" );

        DisplayerSettingsJSONMarshaller marshaller = DisplayerSettingsJSONMarshaller.get();

        DisplayerSettings settings = json != null ? marshaller.fromJsonString( json ) : null;
        DisplayerEditor editor = beanManager.lookupBean( DisplayerEditor.class ).newInstance();

        editor.getLookupEditor().setDataSetDefFilter( def -> appDataSets.contains( separator + def.getUUID() + separator ) );

        DisplayerEditorPopup popup = new DisplayerEditorPopup( editor );

        popup.init( settings );
        popup.setOnSaveCommand( getSaveCommand( popup, ctx ) );
        popup.setOnCloseCommand( getCloseCommand( popup, ctx ) );
        return popup;

    }

    @Override
    public String[] getSettingsKeys() {
        return new String[]{ APP_DATASETS };
    }

    @Override
    public String getSettingValue( String key ) {
        return APP_DATASETS.equals( key ) ? appDataSets : "";
    }

    @Override
    public void setSettingValue( String key, String value ) {
        if ( APP_DATASETS.equals( key ) ) {
            appDataSets = value;
        }
    }

    @Override
    public Map<String, String> getMapSettings() {
        Map<String, String> settings = new HashMap<>();
        settings.put( APP_DATASETS, appDataSets );
        return settings;
    }
}
