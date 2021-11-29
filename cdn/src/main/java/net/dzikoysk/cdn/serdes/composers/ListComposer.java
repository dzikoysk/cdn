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
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.NamedElement;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serdes.Composer;
import panda.std.Result;
import panda.std.stream.PandaStream;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.List;

import static net.dzikoysk.cdn.module.standard.StandardOperators.ARRAY_SEPARATOR;
import static panda.std.Result.error;
import static panda.std.Result.ok;
import static panda.utilities.StringUtils.EMPTY;

public final class ListComposer implements Composer<List<Object>> {

    @Override
    public Result<List<Object>, Exception> deserialize(CdnSettings settings, Element<?> source, AnnotatedType type, List<Object> defaultValue, boolean entryAsRecord) {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;

            if (entry.getPieceValue().trim().endsWith("[]")) {
                return ok(Collections.emptyList());
            }

            return error(new UnsupportedOperationException("Cannot deserialize list of " + entry));
        }

        Section section = (Section) source;
        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType collectionType = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];

        return CdnUtils.findComposer(settings, collectionType, null)
                .flatMap(composer -> PandaStream.of(section.getValue())
                        .map(element -> settings.getModules().resolveArrayValue(element))
                        .map(element -> composer.deserialize(settings, element, collectionType, null, true))
                        .filterToResult(Result::errorToOption)
                        .map(stream -> stream.map(Result::get).toList()));
    }

    @Override
    public Result<NamedElement<?>, Exception> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, List<Object> entity) {
        if (entity.isEmpty()) {
            return ok(new Entry(description, key, "[]"));
        }

        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType collectionType = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];

        return CdnUtils.findComposer(settings, collectionType, null)
                .flatMap(serializer -> PandaStream.of(entity)
                        .map(element -> serializer.serialize(settings, Collections.emptyList(), EMPTY, collectionType, element))
                        .filterToResult(Result::errorToOption)
                        .map(stream -> stream
                                .map(Result::get)
                                .map(serializedElement -> settings.getModules().visitArrayValue(serializedElement))
                                .collect(Section.collector(() -> new Section(description, ARRAY_SEPARATOR, key)))));
    }

}
