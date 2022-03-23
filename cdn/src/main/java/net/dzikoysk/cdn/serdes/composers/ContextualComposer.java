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
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.serdes.CdnDeserializer;
import net.dzikoysk.cdn.serdes.CdnSerializer;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.reflect.TargetType;
import panda.std.Result;
import java.util.List;

public final class ContextualComposer implements Composer<Object> {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Result<Object, Exception> deserialize(CdnSettings settings, Element<?> source, TargetType type, Object defaultValue, boolean entryAsRecord) {
        return new CdnDeserializer(settings).deserialize((Section) source, type.getType());
    }

    @Override
    public Result<? extends Element<?>, ? extends Exception> serialize(CdnSettings settings, List<String> description, String key, TargetType type, Object entity) {
        Section section = new Section(description, StandardOperators.OBJECT_SEPARATOR, key);
        return new CdnSerializer(settings).serialize(entity, section);
    }

}
