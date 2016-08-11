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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.livespark.deployment.model.LiveSparkApp;
import org.uberfire.mvp.Command;

@Dependent
public class AppItemPresenter implements IsWidget {

    protected AppItemPresenterView view;

    protected LiveSparkApp app;

    protected Command actionCommand;

    @Inject
    public AppItemPresenter( AppItemPresenterView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    public void init( LiveSparkApp app, Command actionCommand ) {
        this.app = app;
        this.actionCommand = actionCommand;

        view.showApp( app );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void editApp() {
        if ( actionCommand != null ) {
            actionCommand.execute();
        }
    }

    public interface AppItemPresenterView extends IsWidget {

        void setPresenter( AppItemPresenter appItemPresenter );

        void showApp( LiveSparkApp app );
    }
}
