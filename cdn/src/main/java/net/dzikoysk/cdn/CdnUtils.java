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

import net.dzikoysk.cdn.composers.ContextualsComposers;
import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.Exclude;
import net.dzikoysk.cdn.entity.SectionValue;
import net.dzikoysk.cdn.serialization.Composer;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class CdnUtils {

    private CdnUtils() {}

    @SuppressWarnings("unchecked")
    public static Composer<Object> findComposer(CdnSettings settings, Class<?> type, @Nullable Field field) throws Exception {
        Composer<?> composer = null;

        if (field != null && field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            composer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            for (Entry<? extends Class<?>, ? extends Composer<?>> serializerEntry : settings.getComposers().entrySet()) {
                if (type.isAssignableFrom(serializerEntry.getKey())) {
                    composer = serializerEntry.getValue();
                    break;
                }
            }

            if (composer == null) {
                for (Entry<? extends Predicate<Class<?>>, ? extends Composer<?>> dynamicComposer : settings.getDynamicComposers().entrySet()) {
                    if (dynamicComposer.getKey().test(type)) {
                        composer = dynamicComposer.getValue();
                        break;
                    }
                }
            }
        }

        if (type.isAnnotationPresent(SectionValue.class)) {
            composer = new ContextualsComposers();
        }

        if (composer == null) {
            throw new UnsupportedOperationException("Cannot find composer for '" + type  + "' type");
        }

        return (Composer<Object>) composer;
    }

    static boolean isIgnored(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
            return true;
        }

        // ignore Groovy properties
        if (field.getName().startsWith("__$") || field.getName().startsWith("$")) {
            return true;
        }

        return field.isAnnotationPresent(Exclude.class);
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
            if (value.isEmpty() || value.trim().length() != value.length() || value.endsWith(",")) {
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
