package net.dzikoysk.cdn.model;

import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Section extends AbstractConfigurationElement<List<ConfigurationElement<?>>> {

    public Section(String name, List<String> description) {
        this(name, description, new ArrayList<>());
    }

    public Section(String name, List<String> description, List<ConfigurationElement<?>> value) {
        super(name, description, value);
    }

    public <E extends ConfigurationElement<?>> E append(E element) {
        super.value.add(element);
        return element;
    }

    public boolean has(String key) {
        return get(key) != null;
    }

    public List<String> getList(String key) {
        Section section = getSection(key);
        return section == null ? null : section.getList();
    }

    public List<String> getList() {
        List<String> values = new ArrayList<>(getValue().size());

        for (ConfigurationElement<?> element : getValue()) {
            if (element instanceof Entry) {
                values.add(((Entry) element).getRecord());
            }
            else if (!(element instanceof StandaloneDescription)) {
                return null;
            }
        }

        return values;
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
        ConfigurationElement<?> result = null;

        for (ConfigurationElement<?> element : getValue()) {
            if (key.equals(element.getName())) {
                result = element;
                break;
            }
        }

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
