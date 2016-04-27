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
package org.livespark.formmodeler.renderer.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.livespark.formmodeler.common.engine.handling.FieldChangeHandler;
import org.livespark.formmodeler.common.engine.handling.impl.FormFieldImpl;
import org.livespark.formmodeler.common.engine.handling.FormHandler;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.model.impl.relations.SubFormFieldDefinition;
import org.livespark.formmodeler.renderer.client.rendering.FieldLayoutComponent;
import org.livespark.formmodeler.renderer.client.rendering.renderers.relations.subform.SubFormWidget;
import org.livespark.formmodeler.renderer.service.FormRenderingContext;
import org.livespark.formmodeler.renderer.service.Model2FormTransformerService;
import org.livespark.widgets.crud.client.component.formDisplay.IsFormView;
import org.uberfire.mvp.Command;

@Dependent
public class DynamicFormRenderer implements IsWidget, IsFormView {

    public interface DynamicFormRendererView extends IsWidget {
        void setPresenter( DynamicFormRenderer presenter );

        void render( FormRenderingContext context );
        void bind();

        FieldLayoutComponent getFieldLayoutComponentForField( FieldDefinition field );
    }

    private DynamicFormRendererView view;

    private Caller<Model2FormTransformerService> transformerService;

    private FormHandler formHandler;

    private DataBinder binder;

    private FormRenderingContext context;

    @Inject
    public DynamicFormRenderer( DynamicFormRendererView view, Caller<Model2FormTransformerService> transformerService, FormHandler formHandler ) {
        this.view = view;
        this.transformerService = transformerService;
        this.formHandler = formHandler;
    }

    @PostConstruct
    protected void init() {
        view.setPresenter( this );
    }

    public void renderDefaultForm( final Object model ) {
        renderDefaultForm( model, null );
    }

    public void renderDefaultForm( final Object model, final Command callback ) {
        transformerService.call( new RemoteCallback<FormRenderingContext>() {
            @Override
            public void callback( FormRenderingContext context ) {
                context.setModel( model );
                render( context );
                if ( callback != null ) {
                    callback.execute();
                }
            }
        } ).createContext( model );
    }

    public void render ( FormRenderingContext context ) {
        unBind();
        if ( context == null ) {
            return;
        }
        this.context = context;
        view.render( context );
        if ( context.getModel() != null ) {
            bind( context.getModel() );
        }
    }

    public void bind( Object model ) {
        if ( context != null && model != null ) {
            context.setModel( model );
            doBind( model );
            view.bind();
        }
    }

    protected void doBind( Object model ) {
        binder = DataBinder.forModel( context.getModel() );
        formHandler.init( binder );
    }

    protected void bind( Widget input, FieldDefinition field ) {
        doBind( input, field );
    }

    protected void doBind( Widget input, final FieldDefinition field ) {
        if ( isBinded() ) {
            formHandler.registerInput( new FormFieldImpl( field.getName(),
                    field.getBindingExpression(), field.getValidateOnChange(), input) );
        }
    }

    public void addFieldChangeHandler( FieldChangeHandler handler ) {
        addFieldChangeHandler( null, handler );
    }

    public void addFieldChangeHandler( String fieldName, FieldChangeHandler handler ) {
        if ( context != null && isBinded() ) {
            if ( fieldName != null ) {
                FieldDefinition field = context.getRootForm().getFieldByName( fieldName );
                if ( field == null ) {
                    throw new IllegalArgumentException( "Form doesn't contain any field identified by: '" + fieldName + "'" );
                } else {
                    formHandler.addFieldChangeHandler( fieldName, handler );
                }
            } else {
                formHandler.addFieldChangeHandler( handler );
            }
        }
    }

    public void unBind() {
        if ( isBinded() ) {
            doUnbind();
            formHandler.clear();

            for ( FieldDefinition field : context.getRootForm().getFields() ) {
                if ( field instanceof SubFormFieldDefinition ) {
                    FieldLayoutComponent component = view.getFieldLayoutComponentForField( field );
                    SubFormWidget subFormWidget = (SubFormWidget) component.getFieldRenderer().getInputWidget();
                    subFormWidget.unBind();
                }
            }
        }
    }

    protected void doUnbind() {
        if( isBinded() ) {
            binder.unbind();
        }
    }

    @Override
    public void setModel( Object model ) {
        bind( model );
    }

    @Override
    public Object getModel() {
        if ( isBinded() ) {
            return binder.getModel();
        }
        return null;
    }

    public boolean isValid() {
        return formHandler.validate();
    }

    protected Object getBinderModel() {
        return binder.getModel();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    protected boolean isBinded() {
        return binder != null;
    }
}
