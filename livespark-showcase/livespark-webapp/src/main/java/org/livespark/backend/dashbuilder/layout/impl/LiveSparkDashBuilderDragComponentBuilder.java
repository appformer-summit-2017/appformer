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

package org.livespark.backend.dashbuilder.layout.impl;

import java.util.HashMap;
import java.util.Map;

import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.livespark.backend.dashbuilder.layout.LayoutComponentBuilder;

public class LiveSparkDashBuilderDragComponentBuilder extends LayoutComponentBuilder<LiveSparkDashBoardSettings> {
    public LiveSparkDashBuilderDragComponentBuilder( LiveSparkDashBoardSettings component ) {
        super( component );
    }

    @Override
    protected String getLayoutComponentClassName() {
        return "org.livespark.client.layoutEditor.LiveSparkDisplayerDragComponent";
    }

    @Override
    protected Map<String, String> getComponentSettings( LiveSparkDashBoardSettings settings ) {

        String separator = ",";

        String value = separator;

        for ( String dataSet : settings.getApp().getDataSets() ) {
            value += dataSet + separator;
        }

        Map<String, String> result = new HashMap<>();

        result.put( "appDataSets", value );

        DisplayerSettingsJSONMarshaller marshaller = DisplayerSettingsJSONMarshaller.get();

        result.put( "json", marshaller.toJsonString( settings.getSettings() ) );

        return result;
    }

}
