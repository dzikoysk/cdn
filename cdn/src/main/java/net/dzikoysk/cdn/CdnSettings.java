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

import net.dzikoysk.cdn.annotation.MemberResolver;
import net.dzikoysk.cdn.annotation.DefaultMemberResolver;
import net.dzikoysk.cdn.composers.EnumComposer;
import net.dzikoysk.cdn.composers.ListComposer;
import net.dzikoysk.cdn.composers.MapComposer;
import net.dzikoysk.cdn.composers.ReferenceComposer;
import net.dzikoysk.cdn.model.MutableReference;
import net.dzikoysk.cdn.model.MutableReferenceImpl;
import net.dzikoysk.cdn.model.Reference;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;
import net.dzikoysk.cdn.serialization.SimpleComposer;
import net.dzikoysk.cdn.serialization.SimpleDeserializer;
import net.dzikoysk.cdn.serialization.SimpleSerializer;
import panda.utilities.ClassUtils;
import panda.utilities.ObjectUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

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

    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, Composer> composers = new HashMap<>();
    @SuppressWarnings("rawtypes")
    private final Map<Predicate<Class<?>>, Composer> dynamicComposers = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private final Collection<CdnFeature> features = new ArrayList<>();
    private MemberResolver memberResolver = new DefaultMemberResolver();

    {
        withComposer(boolean.class, Object::toString, Boolean::parseBoolean);
        withComposer(int.class, Object::toString, Integer::parseInt);
        withComposer(long.class, Object::toString, Long::parseLong);
        withComposer(float.class, Object::toString, Float::parseFloat);
        withComposer(double.class, Object::toString, Double::parseDouble);
        withComposer(String.class, CdnUtils::stringify, CdnUtils::destringify);

        withComposer(List.class, new ListComposer<>());
        withComposer(ArrayList.class, new ListComposer<>());
        withComposer(LinkedList.class, new ListComposer<>());

        withComposer(Map.class, new MapComposer<>());
        withComposer(HashMap.class, new MapComposer<>());
        withComposer(TreeMap.class, new MapComposer<>());
        withComposer(LinkedHashMap.class, new MapComposer<>());
        withComposer(ConcurrentHashMap.class, new MapComposer<>());

        withComposer(Reference.class, new ReferenceComposer<>());
        withComposer(MutableReference.class, new ReferenceComposer<>());
        withComposer(MutableReferenceImpl.class, new ReferenceComposer<>());

        withDynamicComposer(Class::isEnum, new EnumComposer());
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
    public <T> CdnSettings withComposer(Class<T> type, SimpleSerializer<T> serializer, SimpleDeserializer<T> deserializer) {
        return withComposer(type, (Serializer<T>) serializer, deserializer);
    }

    /**
     * Register composer using serializer and deserializer.
     *
     * @param type the type to serialize
     * @param serializer the serializer instance
     * @param deserializer the deserializer instance
     * @param <T> generic type of serialized type
     * @return settings instance
     */
    public <T> CdnSettings withComposer(Class<T> type, Serializer<T> serializer, Deserializer<T> deserializer) {
        return withComposer(type, new SimpleComposer<>(serializer, deserializer));
    }

    /**
     * Register simple serializer. Simple serializer can serialize only {@link net.dzikoysk.cdn.model.Unit} and {@link net.dzikoysk.cdn.model.Entry} values.
     *
     * @param type the type to serialize
     * @param composer the serializer instance
     * @param <T> generic type of serialized type
     * @return settings instance
     */
    @SuppressWarnings("unchecked")
    public <T> CdnSettings withComposer(Class<? super T> type, Composer<T> composer) {
        composers.put(type, composer);

        if (type.isPrimitive()) {
            withComposer((Class<T>) ClassUtils.getNonPrimitiveClass(type), composer);
        }

        return this;
    }

    /**
     * Register dynamic composer that may parse range of classes.
     *
     * @param filter composer filter
     * @param composer the serializer instance
     * @return settings instance
     */
    public CdnSettings withDynamicComposer(Predicate<Class<?>> filter, Composer<?> composer) {
        dynamicComposers.put(filter, composer);
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

    public CdnSettings withAnnotationResolver(MemberResolver resolver) {
        this.memberResolver = resolver;
        return this;
    }

    public Collection<? extends CdnFeature> getFeatures() {
        return features;
    }

    public Map<? extends String, ? extends String> getPlaceholders() {
        return placeholders;
    }

    public Map<? extends Predicate<Class<?>>, ? extends Composer<?>> getDynamicComposers() {
        return ObjectUtils.cast(dynamicComposers);
    }

    public Map<? extends Class<?>, ? extends Composer<?>> getComposers() {
        return ObjectUtils.cast(composers);
    }

    public MemberResolver getAnnotationResolver() {
        return memberResolver;
    }
}
