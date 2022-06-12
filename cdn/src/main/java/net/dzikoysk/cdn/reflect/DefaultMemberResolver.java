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
import panda.std.Option;
import panda.std.stream.PandaStream;
import panda.utilities.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultMemberResolver implements MemberResolver {

    private final Visibility visibilityToMatch;

    public DefaultMemberResolver(Visibility visibilityToMatch) {
        this.visibilityToMatch = visibilityToMatch;
    }

    @Override
    public AnnotatedMember fromField(@NotNull Class<?> type, @NotNull Field field) {
        return new FieldMember(field, this);
    }

    @Override
    public AnnotatedMember fromProperty(@NotNull Class<?> type, @NotNull String propertyName) throws NoSuchMethodException {
        Method getter = type.getMethod("get" + propertyName);
        Method setter = type.getMethod("set" + propertyName, getter.getReturnType());

        return new MethodMember(setter, getter, this);
    }

    @Override
    public List<AnnotatedMember> getFields(@NotNull Class<?> type) {
        return PandaStream.of(ReflectUtils.getAllFields(type))
                .map(field -> fromField(type, field))
                .toList();
    }

    @Override
    public List<AnnotatedMember> getProperties(@NotNull Class<?> type) {
        return PandaStream.of(type.getMethods())
                .filterNot(CdnUtils::isIgnored)
                .map(Method::getName)
                .filter(name -> name.startsWith("get"))
                .map(StringUtils::capitalize)
                .map(name -> name.substring(3))
                .flatMap(name -> Option.attempt(NoSuchMethodException.class, () -> fromProperty(type, name)))
                .toList();
    }

    @Override
    public Visibility getVisibilityToMatch() {
        return this.visibilityToMatch;
    }

}
