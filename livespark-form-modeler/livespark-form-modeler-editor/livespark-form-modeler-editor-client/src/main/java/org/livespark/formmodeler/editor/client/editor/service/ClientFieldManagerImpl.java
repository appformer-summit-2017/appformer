/*
 * Copyright 2015 JBoss Inc
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
package org.livespark.formmodeler.editor.client.editor.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.service.AbstractFieldManager;

/**
 * @author Pere Fernandez <pefernan@redhat.com>
 */
@ApplicationScoped
public class ClientFieldManagerImpl extends AbstractFieldManager {

    private Map<String, FieldProvider> fieldProviders = new HashMap<>();

    @PostConstruct
    protected void init() {
        Collection<SyncBeanDef<FieldProvider>> providerDefs = IOC.getBeanManager().lookupBeans( FieldProvider.class );

        for ( SyncBeanDef<FieldProvider> providerDef : providerDefs ) {
            FieldProvider provider = providerDef.getInstance();
            fieldProviders.put( provider.supportedFieldType(), provider );
            registerFieldDefinition( provider.newInstance() );
        }
    }

    @Override
    protected FieldDefinition createNewInstance(FieldDefinition definition) throws Exception {
        if ( definition == null ) return null;

        FieldProvider provider = fieldProviders.get( definition.getCode() );

        if ( provider != null ) {
            return provider.newInstance();
        }

        return null;
    }
}
