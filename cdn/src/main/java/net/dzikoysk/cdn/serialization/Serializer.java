package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents process of converting Java object into configuration element
 *
 * @param <T> the type of serialized value
 */
@FunctionalInterface
public interface Serializer<T> {

    Element<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception;

}
