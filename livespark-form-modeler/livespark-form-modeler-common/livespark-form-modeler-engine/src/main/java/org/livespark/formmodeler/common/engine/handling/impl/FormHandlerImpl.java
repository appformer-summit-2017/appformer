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

package org.livespark.formmodeler.common.engine.handling.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;
import org.livespark.formmodeler.common.engine.handling.FieldChangeHandler;
import org.livespark.formmodeler.common.engine.handling.FieldChangeHandlerManager;
import org.livespark.formmodeler.common.engine.handling.FormFieldProvider;
import org.livespark.formmodeler.common.engine.handling.FieldStyleHandler;
import org.livespark.formmodeler.common.engine.handling.FormField;
import org.livespark.formmodeler.common.engine.handling.FormHandler;
import org.livespark.formmodeler.common.engine.handling.FormValidator;
import org.livespark.formmodeler.common.engine.handling.IsNestedModel;

@Dependent
public class FormHandlerImpl implements FormHandler, FormFieldProvider {

    protected FormValidator validator;

    protected FieldStyleHandler fieldStyleHandler;

    protected FieldChangeHandlerManager fieldChangeManager;

    protected DataBinder binder;

    protected List<FormField> formFields = new ArrayList<>();

    protected List<PropertyChangeUnsubscribeHandle> unsubscribeHandlers = new ArrayList<>();

    @Inject
    public FormHandlerImpl( FormValidator validator, FieldStyleHandler fieldStyleHandler ) {
        this.validator = validator;
        this.fieldStyleHandler = fieldStyleHandler;
        this.validator.setFormFieldProvider( this );
        fieldChangeManager = new FieldChangeHandlerManagerImpl( validator );
    }

    @Override
    public void init( DataBinder binder ) {
        this.binder = binder;
    }

    @Override
    public void registerInput( FormField formField ) {
        assert formField != null;

        String fieldName = formField.getFieldName();
        IsWidget widget = formField.getWidget();

        formFields.add( formField );

        binder.bind( widget, formField.getFieldBinding() );

        fieldChangeManager.registerField( formField.getFieldName(), formField.isValidateOnChange() );

        if ( widget instanceof IsNestedModel ) {
            IsNestedModel nestedModelWidget = (IsNestedModel) widget;
            nestedModelWidget.addFieldChangeHandler( new FieldChangeHandler() {
                @Override
                public void onFieldChange( String childFieldName, Object newValue ) {
                    fieldChangeManager.processFieldChange( fieldName + "." + childFieldName,
                            newValue, getModel() );
                }
            } );

        } else {
            PropertyChangeUnsubscribeHandle unsubscribeHandle = binder.addPropertyChangeHandler(
                    formField.getFieldBinding(), new PropertyChangeHandler() {
                        @Override
                        public void onPropertyChange( PropertyChangeEvent event ) {
                            fieldChangeManager.processFieldChange( fieldName,
                                    event.getNewValue(),
                                    binder.getModel() );
                        }
                    });
            unsubscribeHandlers.add( unsubscribeHandle );
        }
    }

    public void addFieldChangeHandler( FieldChangeHandler handler ) {
        addFieldChangeHandler( null, handler );
    }

    public void addFieldChangeHandler( String fieldName, FieldChangeHandler handler ) {
        if ( fieldName != null ) {
            fieldChangeManager.addFieldChangeHandler( fieldName, handler );
        } else {
            fieldChangeManager.addFieldChangeHandler( handler );
        }
    }

    @Override
    public boolean validate() {
        return validator.validate( getModel() );
    }

    @Override
    public boolean validate( String propertyName ) {
        return validator.validate( propertyName, getModel() );
    }

    @Override
    public void clear() {
        for ( PropertyChangeUnsubscribeHandle handle : unsubscribeHandlers ) {
            handle.unsubscribe();
        }
        unsubscribeHandlers.clear();
        formFields.clear();
        fieldChangeManager.clear();
    }

    public Object getModel() {
        return binder.getModel();
    }

    @Override
    public FormField findFormField( String fieldName ) {

        for ( FormField field : formFields ) {
            if ( field.getFieldName().equals( fieldName ) || field.getFieldBinding().equals( fieldName ) ) {
                return field;
            }
        }

        return null;
    }

    @Override
    public Collection<FormField> getAll() {
        return formFields;
    }
}
