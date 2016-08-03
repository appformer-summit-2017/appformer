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
import java.util.Timer;
import java.util.TimerTask;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.event.Observes;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.livespark.client.shared.AppReady;

@ApplicationScoped
public class DasbuilderTest {


    public void onDeploy( @Observes AppReady event ) {
        new Timer(  ).schedule( new TimerTask() {
            @Override
            public void run() {
                DataSetProviderRegistry dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();


                DataSetProviderType type = dataSetProviderRegistry.getProviderTypeByName( "SQL" );
                if ( type == null ) {
                    throw new IllegalArgumentException( "Provider not supported: SQL" );
                }

                EntityManager entityManager = null;
                try {

                    DataSource dataSource = (DataSource) new InitialContext().lookup( "java:jboss/datasources/ExampleDS" );

                    Connection connection = dataSource.getConnection( "sa", "sa" );

                    String[] types = {"TABLE"};
                    ResultSet rs = connection.getMetaData().getTables(null, null, "%", types);

                    DataSetDefRegistry registry = DataSetCore.get().getDataSetDefRegistry();
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");

                        if ( tableName.contains( "_" ) || registry.getDataSetDef( tableName ) != null ) {
                            continue;
                        }

                        SQLDataSetDef dataSetDef = (SQLDataSetDef) type.createDataSetDef();
                        dataSetDef.setDataSource( "java:jboss/datasources/ExampleDS" );
                        dataSetDef.setDbTable( tableName );

                        dataSetDef.setUUID( tableName );
                        dataSetDef.setName( tableName );


                        registry.registerDataSetDef( dataSetDef );
                        System.out.println( "Deployed DataSet for '" + tableName + "'");
                    }
                } catch ( NamingException e ) {
                    e.printStackTrace();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }

                if ( entityManager == null) {
                    return;
                }

                for ( EntityType<?> entity : entityManager.getEntityManagerFactory().getMetamodel().getEntities() ) {
                    System.out.println( entity.getName() );
                    System.out.println( entity.getJavaType().getName() );
                    SQLDataSetDef dataSetDef = (SQLDataSetDef) type.createDataSetDef();
                    dataSetDef.setDataSource( "java:jboss/datasources/ExampleDS" );
                    //dataSetDef.setDbTable(  );
                    DataSetCore.get().getDataSetDefRegistry().registerDataSetDef( dataSetDef );
                }
            }
        }, 120 * 1000 );



    }
}
