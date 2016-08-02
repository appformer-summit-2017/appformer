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

package org.livespark.formmodeler.rendering.client.view;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.livespark.formmodeler.rendering.client.shared.FormModel;
import org.livespark.formmodeler.rendering.client.shared.LiveSparkRestService;
import org.livespark.widgets.crud.client.component.CrudActionsHelper;
import org.livespark.widgets.crud.client.component.CrudComponent;
import org.livespark.widgets.crud.client.component.formDisplay.IsFormView;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public abstract class ListView<M, F extends FormModel> extends Composite {

    @Inject
    protected SyncBeanManager beanManager;

    @DataField
    protected FlowPanel content = createFlowPanel();

    @Inject
    protected CrudComponent crudComponent;

    protected List<M> crudItems;

    protected FormView<F> currentForm;

    AsyncDataProvider<M> dataProvider;

    protected final CrudActionsHelper crudActionsHelper = new ListViewCrudActionsHelper();

    public void init() {
        dataProvider = new AsyncDataProvider<M>() {
            @Override
            protected void onRangeChanged( final HasData<M> hasData ) {
                if ( crudItems != null ) {
                    updateRowCount( crudItems.size(), true );
                    updateRowData( 0, crudItems );
                } else {
                    updateRowCount( 0, true );
                    updateRowData( 0, new ArrayList<M>() );
                }
            }
        };

        crudComponent.init( crudActionsHelper );
        crudComponent.setEmbedded( false );

        content.add( crudComponent );

        loadData( new RemoteCallback<List<M>>() {

            @Override
            public void callback( final List<M> response ) {
                loadItems( response );
            }
        } );
    }

    /*
     * Is overridable for testing.
     */
    protected <S extends LiveSparkRestService<M>, R> S createRestCaller( final RemoteCallback<R> callback ) {
        return org.jboss.errai.enterprise.client.jaxrs.api.RestClient.create( this.<S>getRemoteServiceClass(), callback );
    }

    /*
     * Is overridable for testing.
     */
    protected FlowPanel createFlowPanel() {
        return new FlowPanel();
    }

    protected void loadData( final RemoteCallback<List<M>> callback ) {
        createRestCaller( callback ).load();
    }

    public void loadItems(final List<M> itemsToLoad) {
        this.crudItems = itemsToLoad;
        initCrud();
    }

    protected void initCrud() {
        crudComponent.refresh();
    }

    public FormView<F> getForm() {
        final SyncBeanDef<? extends FormView<F>> beanDef = beanManager.lookupBean( getFormType() );
        return beanDef.getInstance();
    }

    protected abstract Class<? extends FormView<F>> getFormType();

    public abstract String getListTitle();

    public abstract String getFormTitle();

    protected abstract String getFormId();

    protected abstract <S extends LiveSparkRestService<M>> Class<S> getRemoteServiceClass();

    public abstract List<ColumnMeta> getCrudColumns();

    public abstract M getModel( F formModel );

    public abstract F createFormModel( M model );

    protected class ListViewCrudActionsHelper implements CrudActionsHelper {

        private Integer editedIndex = -1;

        @Override
        public boolean showEmbeddedForms() {
            return false;
        }

        @Override
        public int getPageSize() {
            return 10;
        }

        @Override
        public boolean isAllowCreate() {
            return true;
        }

        @Override
        public boolean isAllowEdit() {
            return true;
        }

        @Override
        public boolean isAllowDelete() {
            return true;
        }

        @Override
        public List<ColumnMeta> getGridColumns() {
            return getCrudColumns();
        }

        @Override
        public AsyncDataProvider<?> getDataProvider() {
            return dataProvider;
        }

        @Override
        public IsFormView getCreateInstanceForm() {
            currentForm = getForm();
            return currentForm;
        }

        @Override
        public void createInstance() {
            createRestCaller(
                    new RemoteCallback<M>() {
                        @Override
                        public void callback( final M response ) {
                            crudItems.add( response );
                            crudComponent.refresh();
                        }
                    } ).create( getModel( currentForm.getModel() ) );
        }

        @Override
        public IsFormView getEditInstanceForm( final Integer index ) {
            editedIndex = index;
            currentForm = getForm();

            BindableProxy<M> proxy = (BindableProxy<M>) BindableProxyFactory.getBindableProxy( crudItems.get( index ) );

            currentForm.setModel( createFormModel( proxy.deepUnwrap() ) );
            return currentForm;
        }

        @Override
        public void editInstance() {
            createRestCaller(
                    new RemoteCallback<Boolean>() {
                        @Override
                        public void callback( final Boolean response ) {
                            if ( editedIndex == -1 ) return;
                            crudItems.set( editedIndex, getModel( currentForm.getModel() ) );
                            crudComponent.refresh();
                        }
                    } ).update( getModel( currentForm.getModel() ) );
        }

        @Override
        public void deleteInstance( final int index ) {
            final M model = crudItems.get( index );
            createRestCaller(
                    new RemoteCallback<Boolean>() {
                        @Override
                        public void callback( final Boolean response ) {
                            if ( response ) {
                                crudItems.remove( model );
                                crudComponent.refresh();
                            }
                        }
                    } ).delete( model );
        }
    };
}
