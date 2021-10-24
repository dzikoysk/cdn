/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface AnnotatedMember {

    void setValue(Object value) throws IllegalAccessException, InvocationTargetException;

    Object getValue() throws IllegalAccessException, InvocationTargetException;

    boolean isAnnotationPresent(Class<? extends Annotation> annotation);

    <A extends Annotation> List<A> getAnnotationsByType(Class<A> annotation);

    <A extends Annotation> A getAnnotation(Class<A> annotation);

    AnnotatedType getAnnotatedType();

    Class<?> getType();

    String getName();

    Object getInstance();

}
