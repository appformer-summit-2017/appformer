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

package org.livespark.deployment.client.perspectives.administration.explorer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.livespark.deployment.model.LiveSparkApp;

@Dependent
@Templated
public class AppItemPresenterViewImpl extends Composite implements AppItemPresenter.AppItemPresenterView {

    @Inject
    @DataField
    private LinkedGroupItem appLink;


    protected AppItemPresenter appItemPresenter;

    @Override
    public void setPresenter( AppItemPresenter appItemPresenter ) {
        this.appItemPresenter = appItemPresenter;
    }

    @Override
    public void showApp( LiveSparkApp app ) {
        appLink.setText( app.toString() );
    }

    @EventHandler( "appLink" )
    protected void editApp( ClickEvent event ) {
        appItemPresenter.editApp();
    }
}