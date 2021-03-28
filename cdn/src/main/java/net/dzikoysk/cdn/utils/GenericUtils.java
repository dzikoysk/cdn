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

package net.dzikoysk.cdn.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class GenericUtils {

    private GenericUtils() {}

    public static Type[] getGenericTypes(Field field) {
        return getGenericTypes(field.getGenericType());
    }

    public static Type[] getGenericTypes(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }

        throw new IllegalArgumentException("Missing generic signature");
    }

    public static Class<?>[] getGenericClasses(Field field) {
        return getGenericClasses(field.getGenericType());
    }

    public static Class<?>[] getGenericClasses(Type type) {
        return Arrays.stream(getGenericTypes(type))
                .map(GenericUtils::toClass)
                .toArray(Class[]::new);
    }

    public static Class<?> toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        }

        try {
            return Class.forName(type.getTypeName());
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new IllegalArgumentException("Cannot find generic type " + type);
        }
    }

}
