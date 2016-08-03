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

package org.livespark.backend.dashbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.googlecode.gwt.crypto.util.Sys;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.livespark.client.shared.AppReady;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DasbuilderTest {

    private static final Logger logger = LoggerFactory.getLogger( DasbuilderTest.class );

    private PersistenceDescriptorService persistenceDescriptorService;

    @Inject
    public DasbuilderTest( PersistenceDescriptorService persistenceDescriptorService ) {
        this.persistenceDescriptorService = persistenceDescriptorService;
    }

    public void onDeploy( @Observes AppReady event ) {
        new Timer().schedule( new TimerTask() {
            @Override
            public void run() {

                PersistenceDescriptorModel persistenceDescriptor = persistenceDescriptorService.load( event.getProject() );

                if ( persistenceDescriptor == null ) {
                    throw new IllegalArgumentException( "Unable to get Persistence Descriptor for project" );
                }

                PersistenceUnitModel persistenceUnit = persistenceDescriptor.getPersistenceUnit();

                if ( persistenceUnit == null ) {
                    throw new IllegalArgumentException( "Unable to get Persistence Unit for project" );
                }

                String dataSourceName = persistenceUnit.getJtaDataSource();

                if ( StringUtils.isEmpty( dataSourceName ) ) {
                    dataSourceName = persistenceUnit.getNonJtaDataSource();
                }

                if ( StringUtils.isEmpty( dataSourceName )) {
                    throw new IllegalArgumentException( "Unable to load DataSource for project" );
                }

                DataSetProviderRegistry dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();

                DataSetProviderType type = dataSetProviderRegistry.getProviderTypeByName( "SQL" );

                if ( type == null ) {
                    throw new IllegalArgumentException( "Provider not supported: SQL" );
                }

                List<String> entityNames = new ArrayList<String>();

                for ( String className : persistenceUnit.getClasses() ) {
                    if ( className.contains( "." )) {
                        className = className.substring( className.lastIndexOf( "." ) + 1 );
                    }
                    entityNames.add( className.toUpperCase() );
                }

                String projectName = event.getProject().getPom().getGav().toString();

                try {

                    DataSource dataSource = (DataSource) new InitialContext().lookup( dataSourceName );

                    Connection connection = dataSource.getConnection( "sa", "sa" );

                    String[] types = {"TABLE"};
                    ResultSet rs = connection.getMetaData().getTables( null, null, "%", types );

                    DataSetDefRegistry registry = DataSetCore.get().getDataSetDefRegistry();
                    while ( rs.next() ) {
                        String tableName = rs.getString( "TABLE_NAME" );

                        if ( !entityNames.contains( tableName.toUpperCase() ) ) {
                            continue;
                        }

                        SQLDataSetDef dataSetDef = (SQLDataSetDef) type.createDataSetDef();
                        dataSetDef.setDataSource( dataSourceName );
                        dataSetDef.setDbTable( tableName );

                        String name = projectName + " - " + tableName;

                        dataSetDef.setUUID( UUID.randomUUID().toString() );
                        dataSetDef.setName( name );

                        registry.registerDataSetDef( dataSetDef );
                        logger.info( "Deployed DataSet '{}' for table '{}'", name, tableName );
                    }
                } catch ( Exception e ) {
                    logger.error( "Error creating DataSets for project '{}'", projectName, e );
                }
            }
        }, 120 * 1000 );


    }

    public static void main(String[] args ) {
        String className = "org.class.LiveSpark";

        System.out.println( className.substring( className.lastIndexOf( "." ) + 1 ));

        String gav = "org.jbpm:jbpm:1.0";

        System.out.println( gav.replaceAll( ":", "-" ));
    }
}
