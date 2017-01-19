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

package org.kie.appformer.formmodeler.codegen.view.impl.java.inputs.impl;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;

public class CheckBoxHelper extends AbstractInputCreatorHelper<CheckBoxFieldDefinition> {

    @Override
    public String getSupportedFieldTypeCode() {
        return CheckBoxFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public String getInputWidget( CheckBoxFieldDefinition fieldDefinition ) {
        return "org.gwtbootstrap3.client.ui.SimpleCheckBox";
    }

    @Override
    public String getReadonlyMethod( String fieldName, String readonlyParam ) {
        return fieldName + ".setEnabled( !" + readonlyParam + ");";
    }
}
