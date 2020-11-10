package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;

@FunctionalInterface
public interface SimpleDeserializer<T> extends Deserializer<T> {

    @Override
    default T deserialize(ConfigurationElement<?> source, T defaultValue, boolean listEntry) {
        if (!(source instanceof Entry)) {
            throw new UnsupportedOperationException("Simple deserializer can deserialize only entries");
        }

        Entry entry = (Entry) source;
        return deserializeEntry(listEntry ? entry.getRecord() : entry.getValue());
    }

    T deserializeEntry(String source);

}
