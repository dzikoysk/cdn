package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Unit;
import org.panda_lang.utilities.commons.StringUtils;

import java.lang.reflect.Type;
import java.util.List;

@FunctionalInterface
public interface SimpleSerializer<T> extends Serializer<T> {

    @Override
    default Element<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) {
        return StringUtils.isEmpty(key) ? new Unit(serialize(entity)) : new Entry(description, key, serialize(entity));
    }

    String serialize(T entity);

}