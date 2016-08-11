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

package org.livespark.deployment.client.perspectives.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import org.livespark.deployment.model.LiveSparkApp;
import org.uberfire.mvp.Command;

public class AppStatusChecker {
    public static final void checkAppStatus( final LiveSparkApp app, final Command onlineCallback, final Command offlineCommand ) {
        RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, app.getUrl() );
        try {
            builder.sendRequest( null, new RequestCallback() {
                public void onError( Request request, Throwable exception ) {
                    GWT.log( "Error trying to comunicate with " + app.getUrl() );
                    runCommand( offlineCommand );
                }

                public void onResponseReceived( Request request, Response response ) {
                    if ( response.getStatusCode() == 200 ) {
                        runCommand( onlineCallback );
                    } else {
                        runCommand( offlineCommand );
                    }
                }
            } );
        } catch ( RequestException e ) {
            GWT.log( "Error trying to comunicate with " + app.getUrl() );
            runCommand( offlineCommand );
        }
    }

    private static void runCommand( Command command ) {
        if ( command != null ) {
            command.execute();
        }
    }
}
