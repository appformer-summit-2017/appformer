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

package org.livespark.client.shared;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LiveSparkApp {
    private String name;
    private String GAV;
    private String version;
    private String url;
    private List<String> dataSets = new ArrayList<>();
    private LiveSparkAppPage page;

    public LiveSparkApp( @MapsTo( "name" ) String name,
                         @MapsTo( "GAV" ) String GAV,
                         @MapsTo( "version" ) String version,
                         @MapsTo( "url" ) String url ) {
        this.name = name;
        this.GAV = GAV;
        this.version = version;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getGAV() {
        return GAV;
    }

    public void setGAV( String GAV ) {
        this.GAV = GAV;
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

    public List<String> getDataSets() {
        return dataSets;
    }

    public void setDataSets( List<String> dataSets ) {
        this.dataSets = dataSets;
    }

    public LiveSparkAppPage getPage() {
        return page;
    }

    public void setPage( LiveSparkAppPage page ) {
        this.page = page;
    }
}
