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

package net.dzikoysk.cdn.serdes;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.reflect.TargetType;
import panda.std.Result;

import static java.lang.String.format;

/**
 * Represents process of converting simple configuration element (Units and Entries) into the Java object
 *
 * @param <T> the type of deserialized value
 */
@FunctionalInterface
public interface SimpleDeserializer<T> extends Deserializer<T> {

    @Override
    default Result<T, Exception> deserialize(CdnSettings settings, Element<?> source, TargetType type, T defaultValue, boolean entryAsRecord) {
        if (source instanceof Piece) {
            return deserialize(type, CdnUtils.destringify(((Piece) source).getValue()));
        }

        if (source instanceof Entry) {
            Entry entry = (Entry) source;
            return deserialize(type, CdnUtils.destringify(entryAsRecord ? entry.getRaw() : entry.getPieceValue()));
        }

        return Result.error(new UnsupportedOperationException(format("Simple deserializer can deserialize only units (%s from %s)", type, source.getClass())));
    }

    default Result<T, Exception> deserialize(TargetType type, String source) {
        return deserialize(source);
    }

    Result<T, Exception> deserialize(String source);

}
