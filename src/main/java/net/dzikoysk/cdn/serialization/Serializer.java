package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.model.ConfigurationElement;

import java.util.List;

@FunctionalInterface
public interface Serializer<T> {

    ConfigurationElement<?> serialize(String key, T entity, List<String> description);

}
