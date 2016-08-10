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

package org.livespark.backend.dashbuilder.layout;

import java.util.List;

import org.dashbuilder.displayer.DisplayerSettings;
import org.livespark.backend.dashbuilder.layout.impl.LiveSparkAppDragComponentBuilder;
import org.livespark.backend.dashbuilder.layout.impl.LiveSparkDashBoardSettings;
import org.livespark.backend.dashbuilder.layout.impl.LiveSparkDashBuilderDragComponentBuilder;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppPage;

public class LayoutGenerator {
    public static void generateLayout( LiveSparkApp app, List<DisplayerSettings> displayersSettings ) {
        LayoutBuilder builder = LayoutBuilder.getLayoutBuilder( "Home" );

        builder.addComponent( new LiveSparkAppDragComponentBuilder( app ) );

        app.setHome( new LiveSparkAppPage( "Home", builder.build() ) );

        builder = LayoutBuilder.getLayoutBuilder( "DashBoards" );

        builder.newRow();

        int count = 0;
        for ( DisplayerSettings settings : displayersSettings ) {
            if ( count > 1 ) {
                builder.newRow();
                count = 0;
            }

            builder.addComponent( new LiveSparkDashBuilderDragComponentBuilder( new LiveSparkDashBoardSettings( app, settings)));
            count ++;
        }

        app.setDashboards( new LiveSparkAppPage( "DashBoards", builder.build() ) );
    }
}
