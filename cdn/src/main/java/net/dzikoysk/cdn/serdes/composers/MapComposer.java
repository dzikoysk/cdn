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

import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.serdes.Deserializer;
import panda.std.Option;
import panda.std.Pair;
import panda.std.Result;
import panda.std.stream.PandaStream;
import panda.utilities.ObjectUtils;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static panda.std.Result.error;
import static panda.std.Result.ok;

public final class MapComposer implements Composer<Map<Object, Object>> {

    @Override
    public Result<Element<?>, ? extends Exception> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, Map<Object, Object> entity) {
        if (entity.isEmpty()) {
            return ok(new Entry(description, key, "[]"));
        }

        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType[] collectionTypes = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        AnnotatedType keyType = collectionTypes[0];
        AnnotatedType valueType = collectionTypes[1];

        // umm... I know
        return CdnUtils.findComposer(settings, keyType, null)
                .<Exception> mapErr(exception -> new CdnException("Cannot find serializer for key of Map<Key, Value>", exception))
                .merge(
                        CdnUtils.findComposer(settings, valueType, null)
                                .mapErr(exception -> new CdnException("Cannot find serializer for value of Map<Key, Value>", exception)),
                        Pair::of)
                .flatMap(serializers -> PandaStream.of(entity.entrySet())
                        .map(entry -> serializers.getFirst()
                                .serialize(settings, Collections.emptyList(), "", keyType, ObjectUtils.cast(entry.getKey()))
                                .<Exception>mapErr(exception -> new CdnException("Cannot serialize key of map", exception))
                                .flatMap(keyElement -> serializers.getSecond()
                                        .serialize(settings, Collections.emptyList(), keyElement.getValue().toString(), valueType, ObjectUtils.cast(entry.getValue()))
                                        .mapErr(exception -> new CdnException("Cannot serialize value of map", exception))))
                        .filterToResult(Result::errorToOption)
                        .flatMap(allElementsStream -> allElementsStream
                                .map(Result::get)
                                .filterToResult(element -> Option.when(!(element instanceof Entry || element instanceof Section), new CdnException("Unsupported element in map: " + element)))
                                .map(filteredStream -> filteredStream.collect(Section.collector(() -> new Section(description, StandardOperators.OBJECT_SEPARATOR, key))))));
    }

    @Override
    public Result<Map<Object, Object>, Exception> deserialize(CdnSettings settings, Element<?> source, AnnotatedType type, Map<Object, Object> defaultValue, boolean entryAsRecord) {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;
            // String value = entryAsRecord ? entry.get() : entry.getValue();
            String value = entry.getPieceValue();

            if (value.equals("[]") /* || entry.getRecord().equals(value) ?what the f is this even doing here? */) {
                return ok(Collections.emptyMap());
            }

            return error(new CdnException("Cannot deserialize map of " + value));
        }

        Section section = (Section) source;
        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType[] collectionTypes = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        AnnotatedType keyType = collectionTypes[0];
        AnnotatedType valueType = collectionTypes[1];

        return CdnUtils.findComposer(settings, keyType, null)
                .merge(CdnUtils.findComposer(settings, valueType, null), Pair::of)
                .flatMap(serializers -> PandaStream.of(section.getValue())
                        .map(element -> deserializeElement(settings, serializers.getFirst(), keyType, serializers.getSecond(), valueType, element, entryAsRecord))
                        .filterToResult(Result::errorToOption)
                        .map(stream -> stream
                                .map(Result::get)
                                .toMapByPair(LinkedHashMap::new, pair -> pair)));
    }

    private Result<Pair<Object, Object>, Exception> deserializeElement(
            CdnSettings settings,
            Deserializer<?> keyDeserializer,
            AnnotatedType keyType,
            Deserializer<?> valueDeserializer,
            AnnotatedType valueType,
            Element<?> element,
            boolean entryAsRecord
    ) {
        if (element instanceof Entry) {
            Entry entry = (Entry) element;
            return deserialize(settings, entry.getName(), keyDeserializer, keyType, valueDeserializer, valueType, entry.getValue(), entryAsRecord);
        }
        else if (element instanceof Section) {
            return deserialize(settings, ((Section) element).getName(), keyDeserializer, keyType, valueDeserializer, valueType, element, entryAsRecord);
        }
        else {
            return Result.error(new UnsupportedOperationException("Unsupported element in map: " + element));
        }
    }

    private Result<Pair<Object, Object>, Exception> deserialize(
            CdnSettings settings,
            String name,
            Deserializer<?> keyDeserializer,
            AnnotatedType keyType,
            Deserializer<?> valueDeserializer,
            AnnotatedType valueType,
            Element<?> source,
            boolean entryAsRecord
    ) {
        return keyDeserializer.deserialize(settings, new Piece(name), keyType, null, entryAsRecord)
                .merge(valueDeserializer.deserialize(settings, source, valueType, null, entryAsRecord), Pair::of);
    }

}
