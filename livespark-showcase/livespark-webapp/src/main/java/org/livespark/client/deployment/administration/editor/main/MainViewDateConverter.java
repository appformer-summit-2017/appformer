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

package org.livespark.client.deployment.administration.editor.main;

import java.util.Date;

import javax.enterprise.context.Dependent;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.jboss.errai.databinding.client.api.Converter;

@Dependent
public class MainViewDateConverter implements Converter<Date, String> {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    @Override
    public Class<Date> getModelType() {
        return Date.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }

    @Override
    public Date toModelValue( String widgetValue ) {
        return DateTimeFormat.getFormat( DATE_FORMAT ).parse( widgetValue );

    }


    @Override
    public String toWidgetValue( Date modelValue ) {
        return DateTimeFormat.getFormat( DATE_FORMAT ).format( (Date) modelValue );
    }

}
