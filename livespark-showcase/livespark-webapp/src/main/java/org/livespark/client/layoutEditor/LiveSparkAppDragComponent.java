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

package org.livespark.client.layoutEditor;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.shared.event.ModalHideEvent;
import org.gwtbootstrap3.client.shared.event.ModalHideHandler;
import org.gwtbootstrap3.client.ui.Modal;
import org.livespark.client.shared.LiveSparkApp;
import org.uberfire.ext.layout.editor.client.api.HasDragAndDropSettings;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

@Dependent
public class LiveSparkAppDragComponent implements LayoutDragComponent, HasDragAndDropSettings, HasModalConfiguration {

    public static final String APP_NAME = "appName";

    public static final String APP_URL = "url";

    private Map<String, String> settings = new HashMap<>();

    public void setUp( LiveSparkApp liveSparkApp ) {
        if ( liveSparkApp != null ) {
            setSettingValue( APP_NAME, liveSparkApp.getName() );
            setSettingValue( APP_URL, liveSparkApp.getUrl() );
        }
    }

    @Override
    public String[] getSettingsKeys() {
        return new String[] { APP_NAME, APP_URL };
    }

    @Override
    public String getSettingValue( String key ) {
        return settings.get( key );
    }

    @Override
    public void setSettingValue( String key, String value ) {
        if ( APP_URL.equals( key ) || APP_NAME.equals( key ) ) {
            settings.put( key, value );
        }
    }

    @Override
    public Map<String, String> getMapSettings() {
        return settings;
    }

    @Override
    public String getDragComponentTitle() {
        return "LiveSpark App";
    }

    @Override
    public IsWidget getPreviewWidget( RenderingContext ctx ) {
        return getFrame( ctx );
    }

    @Override
    public IsWidget getShowWidget( RenderingContext ctx ) {
        return getFrame( ctx );
    }

    protected IsWidget getFrame( RenderingContext ctx ) {

        checkConfig( ctx );

        String url = settings.get( APP_URL );
        if ( url != null && !url.isEmpty() ) {
            Frame frame = new Frame( url );
            frame.getElement().getStyle().setWidth( 100, Style.Unit.PCT );
            frame.getElement().getStyle().setHeight( 600, Style.Unit.PX );
            frame.getElement().getStyle().setBorderWidth( 0, Style.Unit.PX );
            return frame;
        }
        return new SimplePanel();
    }

    private void checkConfig( RenderingContext ctx ) {
        if ( getSettingValue( APP_URL ) == null ) {
            setSettingValue( APP_URL, ctx.getComponent().getProperties().get( APP_URL ) );
        }
        if ( getSettingValue( APP_NAME ) == null ) {
            setSettingValue( APP_NAME, ctx.getComponent().getProperties().get( APP_NAME ) );
        }
    }

    @Override
    public Modal getConfigurationModal( final ModalConfigurationContext ctx ) {
        Modal modal = new Modal();
        modal.addHideHandler( new ModalHideHandler() {
            @Override
            public void onHide( ModalHideEvent evt ) {
                ctx.configurationFinished();
            }
        } );
        return modal;
    }
}
