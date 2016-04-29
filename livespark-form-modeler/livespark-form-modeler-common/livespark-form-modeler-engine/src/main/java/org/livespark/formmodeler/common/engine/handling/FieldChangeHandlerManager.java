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

package org.livespark.formmodeler.common.engine.handling;

/**
 * Component that handles the form field changes,.
 * @author Pere Fernandez <pefernan@redhat.com>
 */
public interface FieldChangeHandlerManager {

    /**
     * Registers a field name to the field handling engine. Doesn't enable validation when a field change is triggered.
     * @param fieldName The name of the field.
     */
    void registerField( String fieldName );

    /**
     * Registers a field name to the field handling engine and may run the field validation before triggering the
     * field change.
     * If field validation fails it doesn't trigger the field change.
     * @param fieldName The name of the field.
     * @param validateOnChange Determines if field validation must run when the field change is triggered.
     */
    void registerField( String fieldName, boolean validateOnChange );

    /**
     * Adds a FieldChangeHandler to the engine that will be notified when any form field is changed.
     * @param changeHandler It must not be null
     */
    void addFieldChangeHandler( FieldChangeHandler changeHandler );

    /**
     * Adds a FieldChangeHandler to the engine that will be notified when a specific form field is changed.
     * @param fieldName It must not be null
     * @param changeHandler It must not be null
     */
    void addFieldChangeHandler( String fieldName, FieldChangeHandler changeHandler );

    /**
     * Method called when a form field changes it's value. It triggers the field change processing for that specific
     * field.
     * @param fieldName The name of the field that triggered the change.
     * @param newValue The new value of the field
     * @param model The model that's being edited on the form.
     */
    void processFieldChange( String fieldName, Object newValue, Object model );

    /**
     * Clears the component status
     */
    void clear();

    /**
     * Sets the form validator.
     * @param validator
     */
    void setValidator( FormValidator validator );
}
