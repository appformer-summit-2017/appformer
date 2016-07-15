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

package org.livespark.formmodeler.renderer.client.rendering.renderers.image;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.livespark.formmodeler.model.impl.basic.image.ImageFieldDefinition;
import org.livespark.formmodeler.renderer.client.rendering.FieldRenderer;

@Dependent
public class PictureFieldRenderer extends FieldRenderer<ImageFieldDefinition> {

    private PictureInput pictureInput;

    @Inject
    public PictureFieldRenderer( PictureInput pictureInput ) {
        this.pictureInput = pictureInput;
    }

    @Override
    public String getName() {
        return ImageFieldDefinition.CODE;
    }

    @Override
    public void initInputWidget() {
        pictureInput.init( 320, 240 );
    }

    @Override
    public IsWidget getInputWidget() {
        return pictureInput;
    }

    @Override
    public String getSupportedCode() {
        return ImageFieldDefinition.CODE;
    }
}
