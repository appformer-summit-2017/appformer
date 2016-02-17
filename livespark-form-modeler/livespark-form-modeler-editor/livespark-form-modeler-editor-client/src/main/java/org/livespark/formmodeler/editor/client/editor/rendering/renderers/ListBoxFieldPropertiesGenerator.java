/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.livespark.formmodeler.editor.client.editor.rendering.renderers;

import javax.enterprise.context.Dependent;

import org.livespark.formmodeler.model.impl.basic.selectors.ListBoxFieldDefinition;

@Dependent
public class ListBoxFieldPropertiesGenerator extends SelectorFieldPropertiesGenerator<ListBoxFieldDefinition> {

    @Override
    public String getSupportedFieldDefinitionCode() {
        return ListBoxFieldDefinition._CODE;
    }
}
