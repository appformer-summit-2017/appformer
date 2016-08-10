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

import java.util.Map;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class LayoutComponentBuilder<T> {

    protected T component;

    public LayoutComponentBuilder( T component ) {
        this.component = component;
    }

    public LayoutComponent getLayoutComponent() {
        LayoutComponent layoutComponent = new LayoutComponent( getLayoutComponentClassName() );
        layoutComponent.addProperties( getComponentSettings( component ) );
        return layoutComponent;
    }

    protected abstract String getLayoutComponentClassName();

    protected abstract Map<String, String> getComponentSettings( T component );

}
