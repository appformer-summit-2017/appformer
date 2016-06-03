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

package org.livespark.formmodeler.service.impl.fieldProviders;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.enterprise.context.Dependent;

import org.livespark.formmodeler.model.FieldTypeInfo;
import org.livespark.formmodeler.model.impl.basic.selectors.radioGroup.RadioGroupBase;
import org.livespark.formmodeler.model.impl.basic.selectors.radioGroup.StringRadioGroupFieldDefinition;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
@Dependent
public class RadioGroupFieldProvider extends SelectorFieldProvider<RadioGroupBase> {

    @Override
    public String getProviderCode() {
        return RadioGroupBase.CODE;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType( String.class );
        registerPropertyType( Character.class );
        registerPropertyType( char.class );


// TODO: implement this fieldTypes
        registerPropertyType( BigDecimal.class );
        registerPropertyType( BigInteger.class );
        registerPropertyType( Byte.class );
        registerPropertyType( byte.class );
        registerPropertyType( Double.class );
        registerPropertyType( double.class );
        registerPropertyType( Float.class );
        registerPropertyType( float.class );
        registerPropertyType( Integer.class );
        registerPropertyType( int.class );
        registerPropertyType( Long.class );
        registerPropertyType( long.class );
        registerPropertyType( Short.class );
        registerPropertyType( short.class );
    }

    @Override
    public int getPriority() {
        return 7;
    }

    @Override
    public RadioGroupBase getDefaultField() {
        return new StringRadioGroupFieldDefinition();
    }

    @Override
    public RadioGroupBase createFieldByType( FieldTypeInfo typeInfo ) {
        return getDefaultField();
    }
}
