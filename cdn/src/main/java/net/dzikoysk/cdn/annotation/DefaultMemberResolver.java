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

import net.dzikoysk.cdn.CdnUtils;
import panda.std.Option;
import panda.std.stream.PandaStream;
import panda.utilities.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class DefaultMemberResolver implements MemberResolver {

    @Override
    public AnnotatedMember fromField(Object instance, Field field) {
        return new FieldMember(instance, field);
    }

    @Override
    public AnnotatedMember fromProperty(Object instance, String propertyName) throws NoSuchMethodException {
        Method getter = instance.getClass().getMethod("get" + propertyName);
        Method setter = instance.getClass().getMethod("set" + propertyName, getter.getReturnType());

        return new MethodMember(instance, setter, getter);
    }

    @Override
    public List<AnnotatedMember> getProperties(Object instance) {
        return PandaStream.of(instance.getClass().getMethods())
                .filter(CdnUtils::isIgnored)
                .map(Method::getName)
                .filter(name -> name.startsWith("get"))
                .map(StringUtils::capitalize)
                .map(name -> name.substring(3))
                .flatMap(name -> Option.attempt(NoSuchMethodException.class, () -> fromProperty(instance, name)))
                .toList();
    }

}
