/*
 * Copyright 2015 JBoss Inc
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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.marshalling.client.Marshalling;
import org.livespark.client.shared.LiveSparkApp;
import org.livespark.client.shared.LiveSparkAppsManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.layout.editor.client.generator.BootstrapLayoutGenerator;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchScreen(identifier = "LiveSparkAppScreen")
public class LiveSparkAppScreen extends Composite {

	@Inject
	protected Caller<LiveSparkAppsManager> liveSparkAppsManager;

	@Inject
	protected BootstrapLayoutGenerator bootstrapLayoutGenerator;

	private Panel mainPanel = new FlowPanel();

	private PlaceRequest place;

	private LiveSparkApp liveSparkApp;

	@OnStartup
	public void onStartup( final PlaceRequest place ) {

		this.place = place;

		String jsonApp = place.getParameter( "app", "" );

		if ( jsonApp != null && !jsonApp.isEmpty() ) {
			liveSparkApp = (LiveSparkApp) Marshalling.fromJSON( jsonApp );
		}
	}

	@OnOpen
	public void onOpen() {
		if ( liveSparkApp != null ) {
			mainPanel.add( bootstrapLayoutGenerator.build( liveSparkApp.getPage().getTemplate() ) );
		} else {
			Frame appFrame = new Frame( "http://erraiframework.org" );

			appFrame.setWidth("100%");
			appFrame.setHeight("800px");

			mainPanel.add( appFrame );
		}
	}

	@WorkbenchPartView
	public IsWidget getView() {
		return mainPanel;
	}

	@WorkbenchPartTitle
	public String title() {
		if ( liveSparkApp != null ) {
			return liveSparkApp.getName();
		}
		return "Your app";
	}

}
