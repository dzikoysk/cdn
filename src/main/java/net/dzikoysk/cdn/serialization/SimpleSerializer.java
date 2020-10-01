package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.List;

@FunctionalInterface
public interface SimpleSerializer<T> extends Serializer<T> {

    @Override
    default ConfigurationElement<?> serialize(String key, T entity, List<String> description) {
        return Entry.of((StringUtils.isEmpty(key) ? "" : key + ": ") + serializeEntry(entity), description);
    }

    String serializeEntry(T entity);

}
