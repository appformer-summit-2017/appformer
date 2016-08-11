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

package org.livespark.deployment.dashbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.livespark.deployment.dashbuilder.layout.LayoutGenerator;
import org.livespark.deployment.backend.service.build.LiveSparkAppBuilderImpl;
import org.livespark.deployment.model.LiveSparkApp;
import org.livespark.deployment.service.LiveSparkAppsManager;
import org.livespark.deployment.service.events.AppReady;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Specializes
public class DashbuilderAwareLiveSparkAppBuilder extends LiveSparkAppBuilderImpl {

    private static final Logger logger = LoggerFactory.getLogger( DashbuilderAwareLiveSparkAppBuilder.class );

    private PersistenceDescriptorService persistenceDescriptorService;

    @Inject
    public DashbuilderAwareLiveSparkAppBuilder( Event<AppReady> appReadyEvent,
                                                LiveSparkAppsManager liveSparkAppsManager,
                                                PersistenceDescriptorService persistenceDescriptorService ) {
        super( appReadyEvent, liveSparkAppsManager );
        this.persistenceDescriptorService = persistenceDescriptorService;
    }

    @Override
    public void buildAndpublishLiveSparkApp( final LiveSparkApp app, final Project project ) {

        new Timer().schedule( new TimerTask() {
                                  @Override
                                  public void run() {
                                      geneareteDataSets( app, project );
                                      DashbuilderAwareLiveSparkAppBuilder.super.buildAndpublishLiveSparkApp( app, project );
                                  }
                              }, 60 * 1000 );
        List<String> dataSetsNames ;


    }

    protected void geneareteDataSets( final LiveSparkApp app, final Project project ) {
        PersistenceDescriptorModel persistenceDescriptor = persistenceDescriptorService.load( project );

        if ( persistenceDescriptor == null ) {
            throw new IllegalArgumentException( "Unable to get Persistence Descriptor for project" );
        }

        PersistenceUnitModel persistenceUnit = persistenceDescriptor.getPersistenceUnit();

        String dataSourceName = persistenceUnit.getJtaDataSource();

        if ( StringUtils.isEmpty( dataSourceName ) ) {
            dataSourceName = persistenceUnit.getNonJtaDataSource();
        }

        if ( StringUtils.isEmpty( dataSourceName ) ) {
            throw new IllegalArgumentException( "Unable to load DataSource for project" );
        }

        DataSetProviderRegistry dataSetProviderRegistry = DataSetCore.get().getDataSetProviderRegistry();

        DataSetProviderType type = dataSetProviderRegistry.getProviderTypeByName( "SQL" );

        List<String> entityNames = new ArrayList<String>();

        for ( String className : persistenceUnit.getClasses() ) {
            if ( className.contains( "." ) ) {
                className = className.substring( className.lastIndexOf( "." ) + 1 );
            }
            entityNames.add( className.toUpperCase() );
        }

        String gav = project.getPom().getGav().toString();

        try {

            List<DisplayerSettings> displayersSettings = new ArrayList<>();
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

                String name = tableName + " (" + gav + ")";

                dataSetDef.setUUID( UUID.randomUUID().toString() );
                dataSetDef.setName( name );
                dataSetDef.setRefreshTime( "1 second" );
                dataSetDef.setRefreshAlways( true );

                registry.registerDataSetDef( dataSetDef );

                displayersSettings.add( generateSettings( tableName, dataSetDef ) );

                logger.info( "Deployed DataSet '{}' for table '{}'", name, tableName );
            }

            LayoutGenerator.generateLayout( app, displayersSettings );
        } catch ( Exception e ) {
            logger.error( "Error creating DataSets for project '{}'", gav, e );
        }
    }

    protected DisplayerSettings generateSettings( String tableName, SQLDataSetDef dataSetDef ) {
        DisplayerSettings displayerSettings = DisplayerSettingsFactory.newTableSettings()
                .uuid( UUID.randomUUID().toString() )
                .dataset( dataSetDef.getUUID() )
                .title( tableName )
                .titleVisible(false)
                .tablePageSize(20)
                .filterOff(true)
                .refreshOn( 1, true )
                .buildSettings();

        return displayerSettings;
    }
}
