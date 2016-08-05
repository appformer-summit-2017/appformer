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

package org.livespark.backend.server.service.build;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.livespark.client.shared.AppReady;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppsManager;

@Dependent
public class LiveSparkAppBuilderImpl implements LiveSparkAppBuilder {


    private Event<AppReady> appReadyEvent;

    private LiveSparkAppsManager liveSparkAppsManager;

    @Inject
    public LiveSparkAppBuilderImpl( Event<AppReady> appReadyEvent,
                                    LiveSparkAppsManager liveSparkAppsManager ) {
        this.appReadyEvent = appReadyEvent;
        this.liveSparkAppsManager = liveSparkAppsManager;
    }

    @Override
    public void buildAndpublishLiveSparkApp( LiveSparkApp app, Project project ) {

        liveSparkAppsManager.registerApp( app );

        appReadyEvent.fire( new AppReady( app ) );
    }
}
