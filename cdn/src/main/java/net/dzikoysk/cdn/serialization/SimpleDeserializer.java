package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;

import java.lang.reflect.Type;

@FunctionalInterface
public interface SimpleDeserializer<T> extends Deserializer<T> {

    @Override
    default T deserialize(CdnSettings settings, ConfigurationElement<?> source, Type genericType, T defaultValue, boolean entryAsRecord) {
        if (!(source instanceof Entry)) {
            throw new UnsupportedOperationException("Simple deserializer can deserialize only entries");
        }

        Entry entry = (Entry) source;
        return deserializeEntry(entryAsRecord ? entry.getRecord() : entry.getValue());
    }

    T deserializeEntry(String source);

}
