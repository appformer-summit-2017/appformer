/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.appformer.flowset.api.definition.property.general;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.appformer.flowset.api.definition.FlowPropertySet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "name"
)
public class FlowGeneralSet implements FlowPropertySet {

    @org.kie.workbench.common.stunner.core.definition.annotation.Name
    @FieldLabel
    public static final transient String propertySetName = "General";

    @Property
    @FormField
    @Valid
    private Name name;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "name"
    )
    @Valid
    private Documentation documentation;

    public FlowGeneralSet() {
        this(new Name(""),
             new Documentation());
    }

    public FlowGeneralSet(final @MapsTo("name") Name name,
                          final @MapsTo("documentation") Documentation documentation) {
        this.name = name;
        this.documentation = documentation;
    }

    public FlowGeneralSet(final String name,
                          final String documentation) {
        this.name = new Name(name);
        this.documentation = new Documentation(documentation);
    }

    public FlowGeneralSet(String name) {
        this.name = new Name(name);
        this.documentation = new Documentation();
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Name getName() {
        return name;
    }

    public Documentation getDocumentation() {
        return documentation;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    public void setDocumentation(final Documentation documentation) {
        this.documentation = documentation;
    }
}