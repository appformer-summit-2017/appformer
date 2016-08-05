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

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public class LayoutBuilder {

    public static final int MAX_SPAN = 12;

    private String layoutName;

    private List<List<LayoutComponentBuilder>> layout = new ArrayList<>();

    private List<LayoutComponentBuilder> currentRow = new ArrayList<>();

    public LayoutBuilder( String layoutName ) {
        this.layoutName = layoutName;
    }

    public LayoutBuilder newRow() {
        if ( !currentRow.isEmpty() ) {
            layout.add( currentRow );
            currentRow = new ArrayList<>();
        }
        return this;
    }

    public LayoutBuilder addComponent( LayoutComponentBuilder componentBuilder ) {
        if ( componentBuilder != null ) {
            if ( currentRow.size() == MAX_SPAN ) {
                newRow();
            }
            currentRow.add( componentBuilder );
        }
        return this;
    }

    public LayoutTemplate build() {
        newRow();

        LayoutTemplate result = new LayoutTemplate();

        result.setName( layoutName );

        if ( !layout.isEmpty() ) {
            for ( List<LayoutComponentBuilder> row : layout ) {

                int components = row.size();

                int extraSpan = MAX_SPAN % components;

                int minSpan = Math.floorDiv( MAX_SPAN, components ) ;

                LayoutRow layoutRow = new LayoutRow();

                for ( LayoutComponentBuilder build : row ) {
                    int componentSpan = minSpan;

                    if ( extraSpan > 0 ) {
                        componentSpan ++;
                        extraSpan --;
                    }

                    LayoutColumn layoutColumn = new LayoutColumn( String.valueOf( componentSpan ) );
                    layoutColumn.add( build.getLayoutComponent() );

                    layoutRow.add( layoutColumn );

                }

                result.addRow( layoutRow );
            }
        }

        return result;
    }
}
