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

package net.dzikoysk.cdn.reflect;

import net.dzikoysk.cdn.CdnUtils;
import org.jetbrains.annotations.NotNull;
import panda.std.Blank;
import panda.std.Option;
import panda.std.Result;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static panda.std.Blank.BLANK;

public class FieldMember implements AnnotatedMember {

    private final Field field;
    private final MemberResolver resolver;
    private final List<Modifier> ignored;

    public FieldMember(Field field, MemberResolver resolver, List<Modifier> ignored) {
        this.field = field;
        this.resolver = resolver;
        this.ignored = ignored;
    }

    @Override
    public boolean isIgnored() {
        return CdnUtils.isIgnored(field, ignored);
    }

    @Override
    public Result<Blank, ReflectiveOperationException> setValue(@NotNull Object instance, @NotNull Object value) {
        return Result.attempt(ReflectiveOperationException.class, () -> {
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
            return BLANK;
        });
    }

    @Override
    public Result<Option<Object>, ReflectiveOperationException> getValue(@NotNull Object instance) {
        return Result.attempt(ReflectiveOperationException.class, () -> {
            field.setAccessible(true);
            Object value = field.get(instance);
            field.setAccessible(false);

            return Option.of(value);
        });
    }

    @Override
    public boolean isAnnotationPresent(@NotNull Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> @NotNull List<A> getAnnotationsByType(@NotNull Class<A> annotation) {
        return Arrays.asList(field.getAnnotationsByType(annotation));
    }

    @Override
    public <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation) {
        return field.getAnnotation(annotation);
    }

    @Override
    public TargetType getTargetType() {
        return new AnnotatedTargetType(field.getAnnotatedType(), resolver);
    }

    @Override
    public @NotNull AnnotatedType getAnnotatedType() {
        return field.getAnnotatedType();
    }

    @Override
    public @NotNull Class<?> getType() {
        return field.getType();
    }

    @Override
    public @NotNull String getName() {
        return field.getName();
    }

}