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
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.serdes.TargetType;
import panda.std.Result;
import panda.std.reactive.Reference;
import panda.std.reactive.ReferenceUtils;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ReferenceComposer<T> implements Composer<T> {

    @Override
    public Result<T, Exception> deserialize(CdnSettings settings, Element<?> source, TargetType type, T defaultValue, boolean entryAsRecord) {
        Reference<Object> defaultReference = (Reference<Object>) defaultValue;
        TargetType referenceType = type.getAnnotatedActualTypeArguments()[0];

        return CdnUtils.findComposer(settings, referenceType, null)
                .flatMap(deserializer -> deserializer.deserialize(settings, source, referenceType, defaultReference.get(), entryAsRecord))
                .map(value -> {
                    Reference<Object> reference = (Reference<Object>) defaultValue;
                    ReferenceUtils.setValue(reference, value);
                    return (T) MEMBER_ALREADY_PROCESSED;
                });
    }

    @Override
    public Result<? extends Element<?>, Exception> serialize(CdnSettings settings, List<String> description, String key, TargetType type, T entity) {
        Reference<Object> reference = (Reference<Object>) entity;
        TargetType[] referenceTypes = type.getAnnotatedActualTypeArguments();
        TargetType referenceType = referenceTypes[0];

        return CdnUtils.findComposer(settings, referenceType, null)
                .flatMap(serializer -> serializer.serialize(settings, description, key, referenceType, reference.get()));
    }

}
