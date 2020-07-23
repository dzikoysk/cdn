package net.dzikoysk.cdn.model;

import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Section extends AbstractConfigurationElement<Map<String, ConfigurationElement<?>>> {

    public Section(String name, List<String> description) {
        this(name, description, new HashMap<>());
    }

    public Section(String name, List<String> description, Map<String, ConfigurationElement<?>> value) {
        super(name, description, value);
    }

    public <E extends ConfigurationElement<?>> E append(E element) {
        super.value.put(element.getName(), element);
        return element;
    }

    public boolean has(String key) {
        return get(key) != null;
    }

    public Integer getInt(String key) {
        String value = getString(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    public String getString(String key) {
        Entry entry = getEntry(key);
        return entry != null ? entry.getValue() : null;
    }

    public Entry getEntry(String key) {
        return ObjectUtils.cast(get(key));
    }

    public Section getSection(String key) {
        return ObjectUtils.cast(get(key));
    }

    public ConfigurationElement<?> get(String key) {
        ConfigurationElement<?> result = getValue().get(key);

        if (result != null || !key.contains(".")) {
            return result;
        }

        String[] qualifier = StringUtils.split(key, ".");
        Section section = this;

        for (int index = 0; index < qualifier.length - 1 && section != null; index++) {
            section = section.getSection(qualifier[index]);
        }

        if (section != null) {
            return section.get(qualifier[qualifier.length - 1]);
        }

        return null;
    }

}
