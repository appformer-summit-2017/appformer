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

package org.kie.appformer.formmodeler.codegen.view;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import org.kie.appformer.formmodeler.codegen.JavaSourceGenerator;

/**
 * Used for implementations of {@link JavaSourceGenerator} or {@link HTMLTemplateGenerator} that
 * generate a list view for viewing, updating, and deleting entries.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface ListView {

}
