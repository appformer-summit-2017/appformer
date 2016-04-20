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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.model.impl.relations.MultipleSubFormFieldDefinition;
import org.livespark.formmodeler.model.impl.relations.SubFormFieldDefinition;
import org.livespark.formmodeler.renderer.client.handling.FieldChangeHandler;
import org.livespark.formmodeler.renderer.client.rendering.FieldLayoutComponent;
import org.livespark.formmodeler.renderer.client.rendering.renderers.relations.multipleSubform.MultipleSubFormWidget;
import org.livespark.formmodeler.renderer.client.rendering.renderers.relations.subform.SubFormWidget;
import org.livespark.formmodeler.renderer.service.FormRenderingContext;
import org.livespark.formmodeler.renderer.service.Model2FormTransformerService;
import org.livespark.formmodeler.rendering.client.view.validation.FormViewValidator;
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

    private FormViewValidator formViewValidator;

    private DataBinder binder;

    private FormRenderingContext context;

    private List<PropertyChangeUnsubscribeHandle> unsubscribeHandlers = new ArrayList<PropertyChangeUnsubscribeHandle>();

    @Inject
    public DynamicFormRenderer( DynamicFormRendererView view, Caller<Model2FormTransformerService> transformerService, FormViewValidator formViewValidator ) {
        this.view = view;
        this.transformerService = transformerService;
        this.formViewValidator = formViewValidator;
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
    }

    public void bind( Widget input, FieldDefinition field ) {
        doBind( input, field );
    }

    protected void doBind( Widget input, FieldDefinition field ) {
        if ( isBinded() ) {
            binder.bind( input, field.getBindingExpression() );
            formViewValidator.registerInput( field.getName(), input );
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
                    FieldLayoutComponent component = view.getFieldLayoutComponentForField( field );

                    if ( field instanceof SubFormFieldDefinition ) {
                        SubFormWidget subFormWidget = (SubFormWidget) component.getFieldRenderer().getInputWidget();
                        subFormWidget.addFieldChangeHandler( field.getBindingExpression(), handler );
                    } else if ( field instanceof MultipleSubFormFieldDefinition ) {

                        MultipleSubFormWidget widget = (MultipleSubFormWidget) component.getFieldRenderer().getInputWidget();
                        widget.addFieldChangeHandler( handler );

                    } else {
                        registerChangeHandler( field.getModelName(), field.getBindingExpression(), handler );
                    }
                }
            } else {
                registerAnonymousChangeHandler( handler );
            }
        }
    }

    protected void registerAnonymousChangeHandler( final FieldChangeHandler handler ) {
        registerChangeHandler( null, null, handler );
        List<String> registeredModels = new ArrayList<>();
        for ( final FieldDefinition field : context.getRootForm().getFields() ) {
            if ( !registeredModels.contains( field.getModelName() )) {

                registeredModels.add( field.getModelName() );

                FieldLayoutComponent component = view.getFieldLayoutComponentForField( field );

                if ( field instanceof SubFormFieldDefinition ) {
                    SubFormWidget subFormWidget = (SubFormWidget) component.getFieldRenderer().getInputWidget();
                    subFormWidget.addFieldChangeHandler( handler );
                } else if ( field instanceof MultipleSubFormFieldDefinition ) {

                    MultipleSubFormWidget widget = (MultipleSubFormWidget) component.getFieldRenderer().getInputWidget();
                    widget.addFieldChangeHandler( handler );

                } else {
                    if ( field.getBindingExpression().indexOf( '.' ) != -1 ) {
                        registerChangeHandler( field.getModelName(), field.getModelName() + ".**", handler );
                    } else {
                        registerChangeHandler( field.getModelName(), field.getModelName(), handler );
                    }
                }
            }
        }
    }

    protected void registerChangeHandler( String field, String property, FieldChangeHandler handler ) {
        if ( isBinded() ) {
            PropertyChangeUnsubscribeHandle unsubscribeHandle = doRegister( field, property, handler );

            unsubscribeHandlers.add( unsubscribeHandle );
        }
    }

    protected PropertyChangeUnsubscribeHandle doRegister( final String field, String property, FieldChangeHandler handler ) {
        if ( property == null ) {
            return binder.addPropertyChangeHandler( event -> {
                    String fieldName = field != null ? field : event.getPropertyName();
                    handler.onFieldChange( fieldName, event.getNewValue() );
                });
        }
        return binder.addPropertyChangeHandler( property,
                event -> {
                    String fieldName = field != null ? field : event.getPropertyName();
                    handler.onFieldChange( fieldName, event.getNewValue() );
                });
    }

    public void unBind() {
        if ( isBinded() ) {
            doUnbind();
            for ( PropertyChangeUnsubscribeHandle handle : unsubscribeHandlers ) {
                handle.unsubscribe();
            }
            unsubscribeHandlers.clear();
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
        return formViewValidator.validate( getBinderModel() );
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
