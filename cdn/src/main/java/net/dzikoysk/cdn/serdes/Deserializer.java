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
import panda.std.Result;
import java.lang.reflect.AnnotatedType;

/**
 * Represents process of converting configuration element into the Java object
 *
 * @param <T> the type of deserialized value
 * @see net.dzikoysk.cdn.model.Piece
 * @see net.dzikoysk.cdn.model.Entry
 */
@FunctionalInterface
public interface Deserializer<T> {

    Result<T, Exception> deserialize(CdnSettings settings, Element<?> source, TargetType type, T defaultValue, boolean entryAsRecord);

}
