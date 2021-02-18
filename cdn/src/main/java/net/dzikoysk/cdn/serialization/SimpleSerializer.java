package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import org.panda_lang.utilities.commons.StringUtils;

import java.lang.reflect.Type;
import java.util.List;

@FunctionalInterface
public interface SimpleSerializer<T> extends Serializer<T> {

    @Override
    default ConfigurationElement<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) {
        return Entry.of((StringUtils.isEmpty(key) ? "" : key + ": ") + serializeEntry(entity), description);
    }

    String serializeEntry(T entity);

}
