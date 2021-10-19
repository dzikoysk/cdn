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

package net.dzikoysk.cdn;

import net.dzikoysk.cdn.composers.ContextualComposers;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.Exclude;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.shared.AnnotatedMember;
import org.jetbrains.annotations.Nullable;
import panda.utilities.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class CdnUtils {

    private CdnUtils() {}

    public static Composer<Object> findComposer(CdnSettings settings, AnnotatedType type, @Nullable AnnotatedMember member) throws ReflectiveOperationException {
        return findComposer(settings, toClass(type.getType()), type, member);
    }

    @SuppressWarnings("unchecked")
    public static Composer<Object> findComposer(CdnSettings settings, Class<?> clazz, AnnotatedType type, @Nullable AnnotatedMember member) throws ReflectiveOperationException {
        Composer<?> composer = null;

        if (member != null && member.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = member.getAnnotation(CustomComposer.class);
            composer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            for (Entry<? extends Class<?>, ? extends Composer<?>> serializerEntry : settings.getComposers().entrySet()) {
                if (clazz.isAssignableFrom(serializerEntry.getKey())) {
                    composer = serializerEntry.getValue();
                    break;
                }
            }

            if (composer == null) {
                for (Entry<? extends Predicate<Class<?>>, ? extends Composer<?>> dynamicComposer : settings.getDynamicComposers().entrySet()) {
                    if (dynamicComposer.getKey().test(clazz)) {
                        composer = dynamicComposer.getValue();
                        break;
                    }
                }
            }
        }

        if (clazz.isAnnotationPresent(Contextual.class) || type.isAnnotationPresent(Contextual.class)) {
            composer = new ContextualComposers();
        }

        if (composer == null) {
            try {
                clazz.getMethod("getMetaClass");
                throw new UnsupportedOperationException("Cannot find composer for '" + clazz  + "' type. Remember that Groovy does not support @Contextual annotation in generic parameters.");
            } catch (NoSuchMethodException noSuchMethodException) {
                throw new UnsupportedOperationException("Cannot find composer for '" + clazz  + "' type");
            }
        }

        return (Composer<Object>) composer;
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

    public static String getPropertyNameFromMethod(String methodName) {
        methodName = methodName.substring(3);
        methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
        return methodName;
    }

    static boolean isIgnored(Field field) {
        int modifiers = field.getModifiers();

        if (!Modifier.isPublic(modifiers)) {
            return true;
        }

        if (Modifier.isStatic(modifiers)) {
            return true;
        }

        if (Modifier.isTransient(modifiers)) {
            return true;
        }

        // ignore Groovy properties
        if (field.getName().startsWith("__$") || field.getName().startsWith("$")) {
            return true;
        }

        return field.isAnnotationPresent(Exclude.class);
    }

    static boolean isIgnored(Method method) {
        int modifiers = method.getModifiers();

        if (Modifier.isNative(modifiers)) {
            return true;
        }

        if (method.getReturnType().getName().equals("groovy.lang.MetaClass")) {
            return true;
        }

        return method.isAnnotationPresent(Exclude.class);
    }

    public static String destringify(String value) {
        for (String operator : CdnConstants.STRING_OPERATORS) {
            if (value.startsWith(operator) && value.endsWith(operator)) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }

    public static String stringify(String value) {
        if (!value.startsWith("\"") && !value.endsWith("\"")) {
            if (value.isEmpty() || value.trim().length() != value.length() || value.endsWith(",") || value.endsWith("{") || value.endsWith(":")) {
                return "\"" + value + "\"";
            }
        }

        return value;
    }

    public static String readFile(File file, Charset charset) throws IOException {
        if (!file.exists()) {
            return "";
        }

        return new String(Files.readAllBytes(file.toPath()), charset);
    }

}
