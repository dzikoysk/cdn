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

package net.dzikoysk.cdn;

import net.dzikoysk.cdn.composers.ListComposer;
import net.dzikoysk.cdn.composers.MapComposer;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;
import net.dzikoysk.cdn.serialization.SimpleDeserializer;
import net.dzikoysk.cdn.serialization.SimpleSerializer;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Settings used by CDN instance. By default, the {@link net.dzikoysk.cdn.CdnSettings} register serializers&deserializers for the given types:
 *
 * <ul>
 *     <li>Boolean (boolean)</li>
 *     <li>Integer (int)</li>
 *     <li>Long (long)</li>
 *     <li>Float (float)</li>
 *     <li>Double (double)</li>
 *     <li>String</li>
 *     <li>List (ArrayList)</li>
 *     <li>Map (HashMap, LinkedHashMap, ConcurrentHashMap)</li>
 * </ul>
 */
public final class CdnSettings {

    protected final Map<Class<?>, Serializer<Object>> serializers = new HashMap<>();
    protected final Map<Class<?>, Deserializer<Object>> deserializers = new HashMap<>();
    protected final Map<String, String> placeholders = new HashMap<>();
    protected final Collection<CdnFeature> features = new ArrayList<>();

    {
        serializer(boolean.class, Object::toString);
        serializer(Boolean.class, Object::toString);
        deserializer(boolean.class, Boolean::parseBoolean);
        deserializer(Boolean.class, Boolean::parseBoolean);

        serializer(int.class, Object::toString);
        serializer(Integer.class, Object::toString);
        deserializer(int.class, Integer::parseInt);
        deserializer(Integer.class, Integer::parseInt);

        serializer(long.class, Object::toString);
        serializer(Long.class, Object::toString);
        deserializer(long.class, Long::parseLong);
        deserializer(Long.class, Long::parseLong);

        serializer(Float.class, Object::toString);
        serializer(Float.class, Object::toString);
        deserializer(Float.class, Float::parseFloat);
        deserializer(Float.class, Float::parseFloat);
        
        serializer(double.class, Object::toString);
        serializer(Double.class, Object::toString);
        deserializer(double.class, Double::parseDouble);
        deserializer(Double.class, Double::parseDouble);
        
        deserializer(String.class, CdnUtils::destringify);
        serializer(String.class, value -> CdnUtils.stringify(value.toString()));

        ListComposer<Object> listComposer = new ListComposer<>();
        serializer(List.class, listComposer);
        serializer(ArrayList.class, listComposer);
        deserializer(List.class, listComposer);
        deserializer(ArrayList.class, listComposer);

        Composer<Object> mapComposer = new MapComposer<>();
        serializer(Map.class, mapComposer);
        serializer(HashMap.class, mapComposer);
        serializer(LinkedHashMap.class, mapComposer);
        serializer(ConcurrentHashMap.class, mapComposer);
        deserializer(Map.class, mapComposer);
        deserializer(HashMap.class, mapComposer);
        deserializer(LinkedHashMap.class, mapComposer);
        deserializer(ConcurrentHashMap.class, mapComposer);
    }

    /**
     * Build an CDN instance using current settings
     *
     * @return a new CDN instance
     */
    public Cdn build() {
        return new Cdn(this);
    }

    /**
     * Register simple serializer. Simple serializer can serialize only {@link net.dzikoysk.cdn.model.Unit} and {@link net.dzikoysk.cdn.model.Entry} values.
     *
     * @param type the type to serialize
     * @param serializer the serializer instance
     * @param <T> generic type of serialized type
     * @return settings instance
     */
    public <T> CdnSettings serializer(Class<T> type, SimpleSerializer<Object> serializer) {
        return serializer(type, (Serializer<Object>) serializer);
    }

    /**
     * Register serializer.
     *
     * @param type the type to serialize
     * @param serializer the serializer instance
     * @param <T> generic type of serialized type
     * @return settings instance
     */
    public <T> CdnSettings serializer(Class<T> type, Serializer<Object> serializer) {
        serializers.put(type, ObjectUtils.cast(serializer));
        return this;
    }

    /**
     * Register simple deserializer. Simple deserializer can deserialize only {@link net.dzikoysk.cdn.model.Unit} and {@link net.dzikoysk.cdn.model.Entry} values.
     *
     * @param type the type to deserialize
     * @param deserializer the deserializer instance
     * @param <T> generic type of deserialized type
     * @return settings instance
     */
    public <T> CdnSettings deserializer(Class<T> type, SimpleDeserializer<Object> deserializer) {
        return deserializer(type, (Deserializer<Object>) deserializer);
    }

    /**
     * Register simple deserializer.
     *
     * @param type the type to deserialize
     * @param deserializer the deserializer instance
     * @param <T> generic type of deserialized type
     * @return settings instance
     */
    public <T> CdnSettings deserializer(Class<T> type, Deserializer<Object> deserializer) {
        deserializers.put(type, ObjectUtils.cast(deserializer));
        return this;
    }

    /**
     * Register map of placeholders
     *
     * @param placeholders placeholders to register
     * @return settings instance
     */
    public CdnSettings registerPlaceholders(Map<String, String> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    /**
     * Install {@link net.dzikoysk.cdn.CdnFeature} instance.
     *
     * @param feature the extension to install
     * @return settings instance
     */
    public CdnSettings installFeature(CdnFeature feature) {
        features.add(feature);
        return this;
    }

    public Collection<? extends CdnFeature> getFeatures() {
        return features;
    }

    public Map<? extends String, ? extends String> getPlaceholders() {
        return placeholders;
    }

    public Map<? extends Class<?>, ? extends Deserializer<Object>> getDeserializers() {
        return deserializers;
    }

    public Map<? extends Class<?>, ? extends Serializer<Object>> getSerializers() {
        return serializers;
    }

}
