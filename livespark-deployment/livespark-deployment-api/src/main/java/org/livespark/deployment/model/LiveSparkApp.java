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

package org.livespark.deployment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Portable
@Bindable
public class LiveSparkApp {
    private String id;
    private Date deploymendDate;
    private String name;
    private String gav;
    private String version;
    private String url;
    private LiveSparkAppPage home;
    private LiveSparkAppPage dashboards;

    public LiveSparkApp() {
    }

    public LiveSparkApp( @MapsTo( "id" ) String id,
                         @MapsTo( "deploymendDate" ) Date deploymendDate,
                         @MapsTo( "name" ) String name,
                         @MapsTo( "gav" ) String gav,
                         @MapsTo( "version" ) String version,
                         @MapsTo( "url" ) String url ) {
        this.id = id;
        this.deploymendDate = deploymendDate;
        this.name = name;
        this.gav = gav;
        this.version = version;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public Date getDeploymendDate() {
        return deploymendDate;
    }

    public void setDeploymendDate( Date deploymendDate ) {
        this.deploymendDate = deploymendDate;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getGav() {
        return gav;
    }

    public void setGav( String gav ) {
        this.gav = gav;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public LiveSparkAppPage getHome() {
        return home;
    }

    public void setHome( LiveSparkAppPage home ) {
        this.home = home;
    }

    public LiveSparkAppPage getDashboards() {
        return dashboards;
    }

    public void setDashboards( LiveSparkAppPage dashboards ) {
        this.dashboards = dashboards;
    }

    @Override
    public String toString() {
        return name + " (" + version + ")";
    }
}
