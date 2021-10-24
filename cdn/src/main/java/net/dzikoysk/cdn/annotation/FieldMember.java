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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class FieldMember implements AnnotatedMember {

    private final Object instance;
    private final Field field;

    public FieldMember(Object instance, Field field) {
        this.instance = instance;
        this.field = field;
    }

    @Override
    public void setValue(Object value) throws IllegalAccessException {
        field.set(instance, value);
    }

    @Override
    public Object getValue() throws IllegalAccessException {
        return field.get(instance);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> List<A> getAnnotationsByType(Class<A> annotation) {
        return Arrays.asList(field.getAnnotationsByType(annotation));
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return field.getAnnotation(annotation);
    }

    @Override
    public AnnotatedType getAnnotatedType() {
        return field.getAnnotatedType();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Object getInstance() {
        return instance;
    }

}