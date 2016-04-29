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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Form Field representation
 * @author Pere Fernandez <pefernan@redhat.com>
 */
public interface FormField {

    /**
     * Retrieves the name of the form field
     * @return The field name.
     */
    String getFieldName();

    /**
     * Retrieves the binding string for the form field.
     * @return The binding string.
     */
    String getFieldBinding();

    /**
     * Determines if the field must be validated when the value changes or not.
     * @return True to validate the field, false if not.
     */
    boolean isValidateOnChange();

    /**
     * Retrieves the form field Widget.
     * @return
     */
    IsWidget getWidget();
}
