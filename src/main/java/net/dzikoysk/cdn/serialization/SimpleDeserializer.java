package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.model.ConfigurationElement;

@FunctionalInterface
public interface SimpleDeserializer<T> extends Deserializer<T> {

    @Override
    default T deserialize(ConfigurationElement<?> source) {
        return deserializeEntry(source.getValue().toString());
    }

    T deserializeEntry(String source);

}
