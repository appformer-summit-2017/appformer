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

package org.livespark.deployment.dashbuilder.layout.impl;

import java.util.HashMap;
import java.util.Map;

import org.livespark.deployment.dashbuilder.layout.LayoutComponentBuilder;
import org.livespark.deployment.model.LiveSparkApp;

public class LiveSparkAppDragComponentBuilder extends LayoutComponentBuilder<LiveSparkApp> {

    public LiveSparkAppDragComponentBuilder( LiveSparkApp settings ) {
        super( settings );
    }

    @Override
    protected String getLayoutComponentClassName() {
        return "org.livespark.deployment.client.layoutEditor.LiveSparkAppDragComponent";
    }

    @Override
    protected Map<String, String> getComponentSettings( LiveSparkApp settings ) {
        Map<String, String> result = new HashMap<>();

        result.put( "appName", settings.getName() );
        result.put( "url", settings.getUrl() );

        return result;
    }
}
