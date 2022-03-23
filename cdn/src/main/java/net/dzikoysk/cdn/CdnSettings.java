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

import net.dzikoysk.cdn.reflect.DefaultMemberResolver;
import net.dzikoysk.cdn.reflect.MemberResolver;
import net.dzikoysk.cdn.module.CdnModule;
import net.dzikoysk.cdn.module.Modules;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.serdes.Deserializer;
import net.dzikoysk.cdn.serdes.Serializer;
import net.dzikoysk.cdn.serdes.SimpleComposer;
import net.dzikoysk.cdn.serdes.SimpleDeserializer;
import net.dzikoysk.cdn.serdes.SimpleSerializer;
import net.dzikoysk.cdn.serdes.composers.EnumComposer;
import net.dzikoysk.cdn.serdes.composers.ListComposer;
import net.dzikoysk.cdn.serdes.composers.MapComposer;
import net.dzikoysk.cdn.serdes.composers.ReferenceComposer;
import panda.std.Result;
import panda.std.reactive.MutableReference;
import panda.std.reactive.Reference;
import panda.utilities.ClassUtils;
import panda.utilities.ObjectUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static panda.std.Result.ok;

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
    private final Modules modules = new Modules();
    private MemberResolver memberResolver = new DefaultMemberResolver();

    {
        withComposer(boolean.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Boolean.parseBoolean(value)));
        withComposer(byte.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Byte.parseByte(value)));
        withComposer(short.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Short.parseShort(value)));
        withComposer(int.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Integer.parseInt(value)));
        withComposer(long.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Long.parseLong(value)));
        withComposer(float.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Float.parseFloat(value)));
        withComposer(double.class, value -> ok(value.toString()), value -> Result.attempt(Exception.class, () -> Double.parseDouble(value)));
        withComposer(String.class, value -> ok(CdnUtils.stringify(value)), value -> ok(CdnUtils.destringify(value)));
        withComposer(Class.class, value -> ok(value.getName()), value -> Result.attempt(Exception.class, () -> Class.forName(value)));

        withComposer(List.class, new ListComposer());
        withComposer(ArrayList.class, new ListComposer());
        withComposer(LinkedList.class, new ListComposer());

        withComposer(Map.class, new MapComposer());
        withComposer(HashMap.class, new MapComposer());
        withComposer(TreeMap.class, new MapComposer());
        withComposer(LinkedHashMap.class, new MapComposer());
        withComposer(ConcurrentHashMap.class, new MapComposer());

        withComposer(Reference.class, new ReferenceComposer<>());
        withComposer(MutableReference.class, new ReferenceComposer<>());

        withDynamicComposer(Class::isEnum, new EnumComposer());
    }

    /**
     * Build an CDN instance using current settings
     *
     * @return a new CDN instance
     */
    public Cdn build() {
        if (modules.isEmpty()) {
            throw new IllegalStateException("CDN requires at least one registered feature. Use DefaultStandardFeature for standard CDN format");
        }

        return new Cdn(this);
    }

    /**
     * Register simple serializer. Simple serializer can serialize only {@link net.dzikoysk.cdn.model.Piece} and {@link net.dzikoysk.cdn.model.Entry} values.
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
     * Register simple serializer. Simple serializer can serialize only {@link net.dzikoysk.cdn.model.Piece} and {@link net.dzikoysk.cdn.model.Entry} values.
     *
     * @param type the type to serialize
     * @param composer the serializer instance
     * @return settings instance
     */
    public CdnSettings withComposer(Class<?> type, Composer<?> composer) {
        composers.put(type, composer);

        if (type.isPrimitive()) {
            withComposer(ClassUtils.getNonPrimitiveClass(type), composer);
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
     * Install {@link CdnModule} instance.
     *
     * @param module the extension to install
     * @return settings instance
     */
    public CdnSettings registerModule(CdnModule module) {
        modules.addModule(module);
        return this;
    }

    public CdnSettings withAnnotationResolver(MemberResolver resolver) {
        this.memberResolver = resolver;
        return this;
    }

    public CdnModule getModule() {
        return modules;
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
