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

package org.livespark.client.deployment.administration.editor.main;


import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.Emphasis;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.livespark.client.deployment.util.AppStatusChecker;
import org.livespark.client.shared.LiveSparkApp;

@Dependent
@Templated
public class MainPageViewImpl extends Composite implements MainPageView {

    @DataField
    @Bound( property = "id" )
    protected Element id = DOM.createDiv();

    @DataField
    @Bound( property = "name" )
    protected Element name = DOM.createDiv();

    @DataField
    @Bound( property = "version" )
    protected Element version = DOM.createDiv();

    @DataField
    @Bound( property = "gav" )
    protected Element gav = DOM.createDiv();

    @DataField
    @Bound( property = "url" )
    protected Element url = DOM.createDiv();

    @DataField
    protected Element status = DOM.createDiv();

    protected DataBinder<LiveSparkApp> binder;

    @Inject
    public MainPageViewImpl( @AutoBound DataBinder<LiveSparkApp> binder ) {
        this.binder = binder;
    }

    @Override
    public void init( LiveSparkApp liveSparkApp ) {
        Assert.notNull( "App cannot be null ", liveSparkApp );
        binder.setModel( liveSparkApp );

        AppStatusChecker.checkAppStatus( liveSparkApp,
                                         () -> showAppActiveIcon( IconType.CHECK_CIRCLE_O ),
                                         () -> showAppActiveIcon( IconType.BAN ) );

    }

    protected void showAppActiveIcon( IconType type ) {
        Icon icon = new Icon( type );

        if ( type.equals( IconType.BAN ) ) {
            icon.setEmphasis( Emphasis.DANGER );
        } else {
            icon.setEmphasis( Emphasis.SUCCESS );
        }

        status.removeAllChildren();
        status.appendChild( icon.getElement() );
    }
}
