package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.model.ConfigurationElement;

@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(ConfigurationElement<?> source, T defaultValue, boolean listEntry);

}
