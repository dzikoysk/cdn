package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;

import java.lang.reflect.Type;

/**
 * Represents process of converting configuration element into the Java object
 *
 * @param <T> the type of deserialized value
 * @see net.dzikoysk.cdn.model.Unit
 * @see net.dzikoysk.cdn.model.Entry
 */
@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(CdnSettings settings, Element<?> source, Type genericType, T defaultValue, boolean entryAsRecord) throws Exception;

}
