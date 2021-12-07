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

package net.dzikoysk.cdn.serdes.composers;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.serdes.SimpleDeserializer;
import panda.std.Result;
import java.lang.reflect.AnnotatedType;
import java.util.List;

import static java.lang.String.format;
import static panda.std.Result.error;
import static panda.std.Result.ok;

public final class EnumComposer implements Composer<Enum<?>>, SimpleDeserializer<Enum<?>> {

    @Override
    @SuppressWarnings({ "unchecked" })
    public Result<Enum<?>, Exception> deserialize(AnnotatedType type, String source) {
        return searchEnum((Class<Enum<?>>) type.getType(), source);
    }

    public static <T extends Enum<?>> Result<T, Exception> searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(search)) {
                return ok(each);
            }
        }

        return error(new IllegalArgumentException(format("No '%s' enum constant in %s", search, enumeration)));
    }

    @Override
    public Result<Enum<?>, Exception> deserialize(String source) {
        throw new UnsupportedOperationException("Enum deserializer requires enum class");
    }

    @Override
    public Result<? extends Element<?>, Exception> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, Enum<?> entity) {
        return ok(key.isEmpty() ? new Piece(entity.name()) : new Entry(description, key, entity.name()));
    }
    
}
