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

package org.livespark.client.deployment;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.marshalling.client.Marshalling;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppsManager;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.PlaceRequest;

public abstract class BaseLiveSparkAppScreen extends Composite {

    protected String liveSparkAppId;

    protected LiveSparkApp liveSparkApp;

    protected PlaceRequest place;

    protected Caller<LiveSparkAppsManager> liveSparkAppsManager;

    public BaseLiveSparkAppScreen( Caller<LiveSparkAppsManager> liveSparkAppsManager ) {
        this.liveSparkAppsManager = liveSparkAppsManager;
    }

    public void onStartup( final PlaceRequest place ) {
        this.place = place;

        liveSparkAppId = place.getParameter( "appId", "" );
    }

    @OnOpen
    public void onOpen() {
        liveSparkAppsManager.call( new RemoteCallback<LiveSparkApp>() {
            @Override
            public void callback( LiveSparkApp liveSparkApp ) {
                BaseLiveSparkAppScreen.this.liveSparkApp = liveSparkApp;
                render( liveSparkApp );
            }
        } ).getAppById( liveSparkAppId );
    }

    protected abstract void render( LiveSparkApp liveSparkApp);
}
