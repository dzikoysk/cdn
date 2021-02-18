package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.ConfigurationElement;

import java.lang.reflect.Type;

@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(CdnSettings settings, ConfigurationElement<?> source, Type genericType, T defaultValue, boolean listEntry) throws Exception;

}
