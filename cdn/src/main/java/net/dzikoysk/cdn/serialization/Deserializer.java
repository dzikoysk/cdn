package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;

import java.lang.reflect.Type;

@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(CdnSettings settings, Element<?> source, Type genericType, T defaultValue, boolean entryAsRecord) throws Exception;

}
