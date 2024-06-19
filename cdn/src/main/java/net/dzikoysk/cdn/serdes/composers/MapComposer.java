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
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.reflect.TargetType;
import panda.std.Option;
import panda.std.Pair;
import panda.std.Result;
import panda.std.stream.PandaStream;
import panda.utilities.ObjectUtils;

import java.util.*;
import java.util.function.Function;

import static net.dzikoysk.cdn.module.standard.StandardOperators.OBJECT_SEPARATOR;
import static panda.std.Result.error;
import static panda.std.Result.ok;

public final class MapComposer implements Composer<Map<Object, Object>> {

    private static final Function<Element<?>, Option<CdnException>> isSupportedMapElement = element ->
            Option.when(!(element instanceof Entry || element instanceof Section), new CdnException("Unsupported element in map: " + element));

    @Override
    public Result<? extends Element<?>, ? extends Exception> serialize(CdnSettings settings, List<String> description, String key, TargetType type, Map<Object, Object> entity) {
        if (entity.isEmpty()) {
            return ok(new Entry(description, key, "[]"));
        }

        TargetType[] collectionTypes = type.getAnnotatedActualTypeArguments();
        TargetType keyType = collectionTypes[0];
        TargetType valueType = collectionTypes[1];

        return CdnUtils.findPairOfComposers(settings, keyType, null, valueType, null)
                .map(serializers -> new MapComposerContext(settings, serializers.getFirst(), keyType, serializers.getSecond(), valueType, false))
                .flatMap(ctx -> serializeMap(ctx, description, key, entity.entrySet()));
    }

    /**
     * Serializes list of entries into a {@link net.dzikoysk.cdn.model.Section} of entries and subsections
     */
    private Result<? extends Section, ? extends Exception> serializeMap(MapComposerContext context, List<String> description, String key, Collection<? extends Map.Entry<Object, Object>> entries) {
        return PandaStream.of(entries)
                .map(entry -> serializeEntry(context, entry))
                .filterToResult(Result::errorToOption)
                .map(serializedElementsStream -> serializedElementsStream.map(Result::get))
                .flatMap(serializedElementsStream -> serializedElementsStream.filterToResult(isSupportedMapElement))
                .map(filteredStream -> filteredStream.collect(Section.collector(() -> new Section(description, OBJECT_SEPARATOR, key))));
    }

    /**
     * Serialize single map entry into an element
     */
    private Result<? extends Element<?>, CdnException> serializeEntry(MapComposerContext context, Map.Entry<Object, Object> entry) {
        Result<? extends Element<?>, CdnException> serializedKey = context.keyComposer
                .serialize(context.settings, Collections.emptyList(), "", context.keyType, ObjectUtils.cast(entry.getKey()))
                .mapErr(exception -> new CdnException("Cannot serialize key of map", exception));

        return serializedKey.flatMap(keyElement -> context.valueComposer
                .serialize(context.settings, Collections.emptyList(), keyElement.getValue().toString(), context.valueType, ObjectUtils.cast(entry.getValue()))
                .mapErr(exception -> new CdnException("Cannot serialize value of map", exception)));
    }

    @Override
    public Result<Map<Object, Object>, Exception> deserialize(CdnSettings settings, Element<?> source, TargetType type, Map<Object, Object> defaultValue, boolean entryAsRecord) {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;
            String value = CdnUtils.destringify(entry.getPieceValue());

            return value.equals("[]")
                    ? ok(new HashMap<>())
                    : error(new CdnException("Cannot deserialize map of " + value));
        }

        Section section = (Section) source;
        TargetType[] collectionTypes = type.getAnnotatedActualTypeArguments();
        TargetType keyType = collectionTypes[0];
        TargetType valueType = collectionTypes[1];

        return CdnUtils.findPairOfComposers(settings, keyType, null, valueType, null)
                .map(serializers -> new MapComposerContext(settings, serializers.getFirst(), keyType, serializers.getSecond(), valueType, entryAsRecord))
                .flatMap(ctx -> deserializeElements(ctx, section.getValue()));
    }

    /**
     * Maps list of deserialization results into a map of values
     */
    private Result<Map<Object, Object>, CdnException> deserializeElements(MapComposerContext context, Collection<? extends Element<?>> elements) {
        return PandaStream.of(elements)
                .map(element -> deserializeElement(context, element))
                .filterToResult(Result::errorToOption)
                .map(CdnUtils::streamOfResultPairToMap);
    }

    /**
     * Maps any {@link net.dzikoysk.cdn.model.Element} into KEY (name) - VALUE (element) entry
     */
    private Result<Pair<Object, Object>, CdnException> deserializeElement(MapComposerContext context, Element<?> element) {
        if (element instanceof Entry)
            return deserialize(context, ((Entry) element).getName(), element);
        else if (element instanceof Section)
            return deserialize(context, ((Section) element).getName(), element);
        else
            return Result.error(new CdnException("Unsupported element in map: " + element));
    }

    /**
     * Deserializes KEY (String name) - VALUE (Element element) map entry and merges it into one result
     */
    private Result<Pair<Object, Object>, CdnException> deserialize(MapComposerContext context, String name, Element<?> element) {
        Result<?, CdnException> serializedKey = context.keyComposer
                .deserialize(context.settings, new Piece(name), context.keyType, null, context.entryAsRecord)
                .mapErr(exception -> new CdnException("Cannot serialize key of map", exception));

        Result<?, CdnException> serializedValue = context.valueComposer
                .deserialize(context.settings, element, context.valueType, null, context.entryAsRecord)
                .mapErr(exception -> new CdnException("Cannot serialize value of map", exception));

        return serializedKey.merge(serializedValue, Pair::of);
    }

    private static final class MapComposerContext {
        CdnSettings settings;
        Composer<?> keyComposer;
        TargetType keyType;
        Composer<?> valueComposer;
        TargetType valueType;
        boolean entryAsRecord;

        private MapComposerContext(CdnSettings settings, Composer<?> keyComposer, TargetType keyType, Composer<?> valueComposer, TargetType valueType, boolean entryAsRecord) {
            this.settings = settings;
            this.keyComposer = keyComposer;
            this.keyType = keyType;
            this.valueComposer = valueComposer;
            this.valueType = valueType;
            this.entryAsRecord = entryAsRecord;
        }
    }

}
