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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MethodMember implements AnnotatedMember {

    private final Method setter;
    private final Method getter;
    private final MemberResolver resolver;

    public MethodMember(Method setter, Method getter, MemberResolver resolver) {
        this.setter = setter;
        this.getter = getter;
        this.resolver = resolver;
    }

    @Override
    public boolean isIgnored() {
        return CdnUtils.isIgnored(setter) || CdnUtils.isIgnored(getter);
    }

    @Override
    public Result<Blank, ReflectiveOperationException> setValue(@NotNull Object instance, @NotNull Object value) {
        return Result.attempt(ReflectiveOperationException.class, () -> Option.of(setter.invoke(instance, value))).mapToBlank();
    }

    @Override
    public Result<Option<Object>, ReflectiveOperationException> getValue(@NotNull Object instance) {
        return Result.attempt(ReflectiveOperationException.class, () -> Option.of(getter.invoke(instance)));
    }

    @Override
    public boolean isAnnotationPresent(@NotNull Class<? extends Annotation> annotation) {
        return getter.isAnnotationPresent(annotation);
    }

    @Override
    public <A extends Annotation> @NotNull List<A> getAnnotationsByType(@NotNull Class<A> annotation) {
        return Arrays.asList(getter.getAnnotationsByType(annotation));
    }

    @Override
    public <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation) {
        return getter.getAnnotation(annotation);
    }

    @Override
    public TargetType getTargetType() {
        return new AnnotatedTargetType(getAnnotatedType(), resolver);
    }

    @Override
    public @NotNull AnnotatedType getAnnotatedType() {
        return getter.getAnnotatedReturnType();
    }

    @Override
    public @NotNull Class<?> getType() {
        return getter.getReturnType();
    }

    @Override
    public @NotNull String getName() {
        return CdnUtils.getPropertyNameFromMethod(getter.getName());
    }

}