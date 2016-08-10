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

package org.livespark.client.deployment.perspective.screen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.livespark.client.shared.LiveSparkApp;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGenerator;

@Templated
@Dependent
public class LiveSparkAppScreenViewImpl extends Composite implements LiveSparkAppScreen.LiveSparkAppScreenView {

    @DataField
    protected SimplePanel homePageContent = new SimplePanel();

    @DataField
    protected SimplePanel dashboardsContent = new SimplePanel();

    protected BootstrapLayoutGenerator bootstrapLayoutGenerator;

    @Inject
    public LiveSparkAppScreenViewImpl( BootstrapLayoutGenerator bootstrapLayoutGenerator ) {
        this.bootstrapLayoutGenerator = bootstrapLayoutGenerator;
    }

    @Override
    public void init( LiveSparkApp liveSparkApp ) {
        homePageContent.add( bootstrapLayoutGenerator.build( liveSparkApp.getHome().getTemplate() ) );
        dashboardsContent.add( bootstrapLayoutGenerator.build( liveSparkApp.getDashboards().getTemplate() ) );
    }
}
