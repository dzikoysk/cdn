package net.dzikoysk.cdn;

import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;
import net.dzikoysk.cdn.serialization.SimpleDeserializer;
import net.dzikoysk.cdn.serialization.SimpleSerializer;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CdnSettings {

    private final Map<Class<?>, Serializer<Object>> serializers = new HashMap<>();
    private final Map<Class<?>, Deserializer<Object>> deserializers = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private boolean indentationEnabled;

    {
        serializer(String.class, Object::toString);
        serializer(Boolean.class, Object::toString);
        serializer(Integer.class, Object::toString);
        serializer(Double.class, Object::toString);

        deserializer(String.class, string -> string);
        deserializer(Boolean.class, Boolean::parseBoolean);
        deserializer(Integer.class, Integer::parseInt);
        deserializer(Double.class, Double::parseDouble);

        deserializer(List.class, value -> {
            if (value.equals("[]")) {
                return Collections.emptyList();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + value);
        });
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

    public CdnSettings enableIndentationFormatting() {
        this.indentationEnabled = true;
        return this;
    }

    public boolean isIndentationEnabled() {
        return indentationEnabled;
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

    public CDN build() {
        return new CDN(this);
    }

}
