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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CdnSettings {

    private final Map<Class<?>, Serializer<Object>> serializers = new HashMap<>();
    private final Map<Class<?>, Deserializer<Object>> deserializers = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private boolean yamlLikeEnabled;

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
        deserializer(Map.class, mapComposer);
        deserializer(HashMap.class, mapComposer);
        deserializer(LinkedHashMap.class, mapComposer);
    }

    public <T> CdnSettings serializer(Class<T> type, SimpleSerializer<Object> serializer) {
        return serializer(type, (Serializer<Object>) serializer);
    }

    public <T> CdnSettings serializer(Class<T> type, Serializer<Object> serializer) {
        serializers.put(type, ObjectUtils.cast(serializer));
        return this;
    }

    public <T> CdnSettings deserializer(Class<T> type, SimpleDeserializer<Object> deserializer) {
        return deserializer(type, (Deserializer<Object>) deserializer);
    }

    public <T> CdnSettings deserializer(Class<T> type, Deserializer<Object> deserializer) {
        deserializers.put(type, ObjectUtils.cast(deserializer));
        return this;
    }

    public CdnSettings registerPlaceholders(Map<String, String> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    public CdnSettings enableYamlLikeFormatting() {
        this.yamlLikeEnabled = true;
        return this;
    }

    public boolean isYamlLikeEnabled() {
        return yamlLikeEnabled;
    }

    public Map<? extends String, ? extends String> getPlaceholders() {
        return placeholders;
    }

    public Map<Class<?>, Deserializer<Object>> getDeserializers() {
        return deserializers;
    }

    public Map<Class<?>, Serializer<Object>> getSerializers() {
        return serializers;
    }

    public Cdn build() {
        return new Cdn(this);
    }

}
