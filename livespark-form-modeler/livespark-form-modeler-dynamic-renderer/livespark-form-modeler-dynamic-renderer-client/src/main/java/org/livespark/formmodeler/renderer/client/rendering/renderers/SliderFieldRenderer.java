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

package org.livespark.formmodeler.renderer.client.rendering.renderers;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.livespark.formmodeler.model.impl.basic.SliderFieldDefinition;
import org.livespark.formmodeler.renderer.client.rendering.FieldRenderer;
import org.livespark.formmodeler.rendering.client.widgets.Slider;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
@Dependent
public class SliderFieldRenderer extends FieldRenderer<SliderFieldDefinition> {

    private Slider slider;

    @Override
    public String getName() {
        return SliderFieldDefinition.CODE;
    }

    @Override
    public void initInputWidget() {
        slider = new Slider( field.getMin(), field.getMax(), field.getPrecission(), field.getStep() );
        slider.setEnabled( !field.getReadonly() );
    }

    @Override
    public IsWidget getInputWidget() {
        return slider;
    }

    @Override
    protected void addFormGroupContents( FormGroup group ) {
        FormLabel label = new FormLabel();
        label.setText( field.getLabel() );

        label.setFor( slider.getId() );
        group.add( label );

        Row newRow = new Row();

        Column column = new Column( ColumnSize.MD_12 );

        column.add( slider );

        newRow.add( column );

        group.add( newRow );

        HelpBlock helpBlock = new HelpBlock();
        helpBlock.setId( getHelpBlokId( field ) );

        group.add( helpBlock );
    }

    @Override
    public String getSupportedFieldDefinitionCode() {
        return SliderFieldDefinition.CODE;
    }
}
