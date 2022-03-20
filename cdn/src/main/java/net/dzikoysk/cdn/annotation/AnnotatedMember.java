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

import net.dzikoysk.cdn.serdes.TargetType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import panda.std.Blank;
import panda.std.Option;
import panda.std.Result;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.List;

public interface AnnotatedMember {

    boolean isIgnored();

    Result<Blank, ReflectiveOperationException> setValue(@NotNull Object value);

    Result<Option<Object>, ReflectiveOperationException> getValue();

    boolean isAnnotationPresent(@NotNull Class<? extends Annotation> annotation);

    <A extends Annotation> @NotNull List<A> getAnnotationsByType(@NotNull Class<A> annotation);

    <A extends Annotation> @Nullable A getAnnotation(@NotNull Class<A> annotation);

    TargetType getTargetType();

    @NotNull AnnotatedType getAnnotatedType();

    @NotNull Class<?> getType();

    @NotNull String getName();

    @NotNull Object getInstance();

}
