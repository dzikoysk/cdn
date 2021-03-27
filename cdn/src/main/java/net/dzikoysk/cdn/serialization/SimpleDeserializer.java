package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Unit;

import java.lang.reflect.Type;

@FunctionalInterface
public interface SimpleDeserializer<T> extends Deserializer<T> {

    @Override
    default T deserialize(CdnSettings settings, Element<?> source, Type genericType, T defaultValue, boolean entryAsRecord) {
        if (source instanceof Unit) {
            return deserialize((Unit) source);
        }

        if (source instanceof Entry) {
            return deserialize((Unit) source.getValue());
        }

        throw new UnsupportedOperationException("Simple deserializer can deserialize only units (" + genericType + " from " + source.getClass() + ")");
    }

    default T deserialize(Unit unit) {
        return deserialize(unit.getValue());
    }

    T deserialize(String source);

}
