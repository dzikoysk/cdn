package net.dzikoysk.cdn;

import org.panda_lang.utilities.commons.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CdnConfiguration {

    private final Map<Class<?>, Function<Object, String>> serializers = new HashMap<>();
    private final Map<Class<?>, Function<String, Object>> deserializers = new HashMap<>();
    private boolean indentationEnabled;

    {
        serializer(String.class, Function.identity());
        serializer(Boolean.class, Object::toString);
        serializer(Integer.class, Object::toString);
        serializer(Double.class, Object::toString);

        deserializer(String.class, Function.identity());
        deserializer(Boolean.class, Boolean::parseBoolean);
        deserializer(Integer.class, Integer::parseInt);
        deserializer(Double.class, Double::parseDouble);
    }

    public <T> CdnConfiguration serializer(Class<T> type, Function<T, String> serializer) {
        serializers.put(type, ObjectUtils.cast(serializer));
        return this;
    }

    public <T> CdnConfiguration deserializer(Class<T> type, Function<String, T> deserializer) {
        deserializers.put(type, ObjectUtils.cast(deserializer));
        return this;
    }

    public CdnConfiguration enableIndentationFormatting() {
        this.indentationEnabled = true;
        return this;
    }

    public boolean isIndentationEnabled() {
        return indentationEnabled;
    }

    public Map<Class<?>, Function<String, Object>> getDeserializers() {
        return deserializers;
    }

    public Map<Class<?>, Function<Object, String>> getSerializers() {
        return serializers;
    }

    public CDN build() {
        return new CDN(this);
    }

}
