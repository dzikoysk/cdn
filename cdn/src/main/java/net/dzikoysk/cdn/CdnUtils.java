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

import net.dzikoysk.cdn.reflect.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.Exclude;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.reflect.TargetType;
import net.dzikoysk.cdn.serdes.composers.ContextualComposer;
import org.jetbrains.annotations.Nullable;
import panda.std.Pair;
import panda.std.Result;
import panda.std.stream.PandaStream;
import panda.utilities.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static panda.std.Result.error;
import static panda.std.Result.ok;

public final class CdnUtils {

    private CdnUtils() {}

    /**
     *  Find a pair of composers for KEY-VALUE relation, utility method that combines result of {@link #findComposer(CdnSettings, net.dzikoysk.cdn.reflect.TargetType, net.dzikoysk.cdn.reflect.AnnotatedMember)} method
     */
    public static Result<Pair<Composer<Object>, Composer<Object>>, Exception> findPairOfComposers(
            CdnSettings settings,
            TargetType keyType, @Nullable AnnotatedMember keyMember,
            TargetType valueType, @Nullable AnnotatedMember valueMember
    ) {
        return findComposer(settings, keyType, keyMember).merge(findComposer(settings, valueType, valueMember), Pair::of);
    }

    public static Result<Composer<Object>, Exception> findComposer(CdnSettings settings, TargetType type, @Nullable AnnotatedMember member) {
        return findComposer(settings, type.getType(), type, member);
    }

    @SuppressWarnings("unchecked")
    public static Result<Composer<Object>, Exception> findComposer(CdnSettings settings, Class<?> clazz, TargetType type, @Nullable AnnotatedMember member) {
        Composer<?> composer = null;

        if (member != null && member.isAnnotationPresent(CustomComposer.class)) {
            Result<Composer<?>, ReflectiveOperationException> composerInstance = Result.attempt(ReflectiveOperationException.class, () -> {
                CustomComposer customComposer = Objects.requireNonNull(member.getAnnotation(CustomComposer.class));
                return ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
            });

            if (composerInstance.isErr()) {
                return error(composerInstance.getError());
            }

            composer = composerInstance.get();
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

        if (clazz.isAnnotationPresent(Contextual.class) || type.isAnnotationPresent(Contextual.class) || (member != null && member.isAnnotationPresent(Contextual.class))) {
            composer = new ContextualComposer();
        }

        if (composer == null) {
            try {
                clazz.getMethod("getMetaClass");
                return error(new UnsupportedOperationException("Cannot find composer for '" + clazz  + "' type. Remember that Groovy does not support @Contextual annotation in generic parameters"));
            } catch (NoSuchMethodException noSuchMethodException) {
                return error(new UnsupportedOperationException("Cannot find composer for '" + clazz  + "' type"));
            }
        }

        return ok((Composer<Object>) composer);
    }

    public static Class<?> toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        }

        String typeName = type.getTypeName();

        try {
            if (typeName.contains(".")) {
                return Class.forName(type.getTypeName());
            } else {
                return parsePrimitiveType(typeName);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new IllegalArgumentException("Cannot find generic type " + type);
        }
    }

    public static Class<?> parsePrimitiveType(String name) throws ClassNotFoundException {
        switch (name) {
            case "boolean": return boolean.class;
            case "byte": return byte.class;
            case "short": return short.class;
            case "int": return int.class;
            case "long": return long.class;
            case "float": return float.class;
            case "double": return double.class;
            case "char": return char.class;
            case "void": return void.class;
            default: throw new ClassNotFoundException("Unknown primitive type " + name);
        }
    }

    public static String getPropertyNameFromMethod(String methodName) {
        methodName = methodName.substring(3);
        methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
        return methodName;
    }

    public static boolean isIgnored(@Nullable Field field, boolean excludeHiddenProperties) {
        if (field == null) {
            return false;
        }

        int modifiers = field.getModifiers();

        if (excludeHiddenProperties && !Modifier.isPublic(modifiers)) {
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

    public static boolean isIgnored(@Nullable Method method) {
        if (method == null) {
            return false;
        }

        int modifiers = method.getModifiers();

        if (Modifier.isNative(modifiers)) {
            return true;
        }

        if (method.getReturnType().getName().equals("groovy.lang.MetaClass")) {
            return true;
        }

        return method.isAnnotationPresent(Exclude.class);
    }

    public static boolean isKotlinDataClass(Class<?> clazz) {
        try {
            clazz.getMethod("component1");
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static String destringify(String raw) {
        if (raw.length() <= 1) {
            return raw;
        }

        String value = raw.replace(StandardOperators.RAW_LINE_SEPARATOR, StandardOperators.LINE_SEPARATOR);

        for (String operator : StandardOperators.STRING_OPERATORS) {
            if (value.startsWith(operator) && value.endsWith(operator)) {
                return value.substring(1, value.length() - 1);
            }
        }

        return value;
    }

    public static boolean isStringified(String value) {
        return value.startsWith("\"") && value.endsWith("\"");
    }

    public static String stringify(String value) {
        String raw = value.replace(StandardOperators.LINE_SEPARATOR, StandardOperators.RAW_LINE_SEPARATOR);

        if (!isStringified(raw)) {
            if (raw.isEmpty() || raw.trim().length() != raw.length() || raw.endsWith(",") || raw.endsWith("{") || raw.endsWith(":")) {
                return "\"" + raw + "\"";
            }
        }

        return raw;
    }

    public static String forceStringify(String value) {
        if (!isStringified(value)) {
            return "\"" + value + "\"";
        }

        return value;
    }

    public static <K, V, E extends Exception> Map<K, V> streamOfResultPairToMap(PandaStream<Result<Pair<K, V>, E>> stream) {
        return stream
                .filter(Result::isOk)
                .map(Result::get)
                .toMapByPair(LinkedHashMap::new, pair -> pair);
    }

    public static <T, R> R process(Collection<T> processors, R value, BiFunction<T, R, R> handler) {
        for (T processor : processors) {
            value = handler.apply(processor, value);
        }

        return value;
    }

    public static <T> Function<? extends T, T> recapture() {
        return value -> value;
    }

}
