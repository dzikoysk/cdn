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

package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Unit;
import org.panda_lang.utilities.commons.StringUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents process of converting Java object into simple configuration element (Units and Entries)
 *
 * @param <T> the type of serialized value
 * @see net.dzikoysk.cdn.model.Unit
 * @see net.dzikoysk.cdn.model.Entry
 */
@FunctionalInterface
public interface SimpleSerializer<T> extends Serializer<T> {

    @Override
    default Element<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) {
        String result = serialize(entity);

        return StringUtils.isEmpty(key) 
                ? new Unit(serialize(entity)) 
                : new Entry(description, key, result);
    }

    String serialize(T entity);

}
