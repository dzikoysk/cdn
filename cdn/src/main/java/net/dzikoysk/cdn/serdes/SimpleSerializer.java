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
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.reflect.TargetType;
import panda.std.Result;
import panda.utilities.StringUtils;
import java.util.List;

/**
 * Represents process of converting Java object into simple configuration element (Units and Entries)
 *
 * @param <T> the type of serialized value
 * @see net.dzikoysk.cdn.model.Piece
 * @see net.dzikoysk.cdn.model.Entry
 */
@FunctionalInterface
public interface SimpleSerializer<T> extends Serializer<T> {

    @Override
    default Result<Element<?>, Exception> serialize(CdnSettings settings, List<String> description, String key, TargetType type, T entity) {
        return serialize(entity)
                .map(result -> StringUtils.isEmpty(key)
                        ? new Piece(result)
                        : new Entry(description, key, result));
    }

    Result<String, Exception> serialize(T entity);

}
