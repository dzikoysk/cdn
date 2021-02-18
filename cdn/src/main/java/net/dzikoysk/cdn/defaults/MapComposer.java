package net.dzikoysk.cdn.defaults;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.serialization.Composer;

import java.lang.reflect.Type;
import java.util.List;

public final class MapComposer<T> implements Composer<T> {

    @Override
    public T deserialize(CdnSettings settings, ConfigurationElement<?> source, Type genericType, T defaultValue, boolean listEntry) {
        return null;
    }

    @Override
    public ConfigurationElement<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) {
        return null;
    }

}
