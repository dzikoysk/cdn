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

package net.dzikoysk.cdn.composers;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnDeserializer;
import net.dzikoysk.cdn.CdnSerializer;
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Composer;

import java.lang.reflect.AnnotatedType;
import java.util.List;

public final class ContextualComposers implements Composer<Object> {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object deserialize(CdnSettings settings, Element<?> source, AnnotatedType type, Object defaultValue, boolean entryAsRecord) throws ReflectiveOperationException {
        return new CdnDeserializer(settings).deserialize(CdnUtils.toClass(type.getType()), (Section) source);
    }

    @Override
    public Element<?> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, Object entity) throws Exception {
        Section section = new Section(description, CdnConstants.OBJECT_SEPARATOR, key);
        return new CdnSerializer(settings).serialize(section, entity);
    }

}
