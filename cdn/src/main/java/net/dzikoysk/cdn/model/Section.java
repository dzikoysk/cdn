package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnUtils;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Section extends AbstractConfigurationElement<List<ConfigurationElement<?>>> {

    private final String[] operators;

    public Section(String name, List<String> description) {
        this(CdnConstants.OBJECT_SEPARATOR, name, description);
    }

    public Section(String[] operators, String name, List<String> description) {
        this(operators, name, description, new ArrayList<>());
    }

    public Section(String name, List<String> description, List<ConfigurationElement<?>> value) {
        this(CdnConstants.OBJECT_SEPARATOR, name, description, value);
    }

    public Section(String[] operators, String name, List<String> description, List<ConfigurationElement<?>> value) {
        super(name, description, value);
        this.operators = operators;
    }

    public <E extends ConfigurationElement<?>> E append(E element) {
        super.value.add(element);
        return element;
    }

    public boolean has(String key) {
        return get(key) != null;
    }

    public int size() {
        return getValue().size();
    }

    public ConfigurationElement<?> get(int index) {
        return getValue().get(index);
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

    public List<String> getList(String key) {
        return getList(key, Collections.emptyList());
    }

    public List<String> getList(String key, List<String> defaultValue) {
        Section section = getSection(key);
        return section != null ? section.getList() : defaultValue;
    }

    public List<String> getList() {
        List<String> values = new ArrayList<>(getValue().size());
        int listOperators = 0;

        for (ConfigurationElement<?> element : getValue()) {
            if (element instanceof Entry) {
                Entry entry = (Entry) element;
                String record = entry.getRecord();

                if (record.startsWith(CdnConstants.LIST)) {
                    listOperators++;
                }

                if (record.endsWith(CdnConstants.SEPARATOR)) {
                    record = record.substring(0, record.length() - CdnConstants.SEPARATOR.length());
                }

                values.add(record);
            }
        }

        for (int index = 0; index < values.size(); index++) {
            String element = values.get(index);

            if (listOperators == values.size()) {
                element = element.substring(1).trim();
            }

            values.set(index, CdnUtils.destringify(element));
        }

        return values;
    }

    public Option<Boolean> getBoolean(String key) {
        return Option.of(getBoolean(key, null));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getString(key)
                .map(Boolean::parseBoolean)
                .orElseGet(defaultValue);
    }

    public Option<Integer> getInt(String key) {
        return Option.of(getInt(key, null));
    }

    public Integer getInt(String key, Integer defaultValue) {
        return getString(key)
                .map(Integer::parseInt)
                .orElseGet(defaultValue);
    }

    public String getString(String key, String defaultValue) {
        Entry entry = getEntry(key);
        return entry != null ? entry.getValue() : defaultValue;
    }

    public Option<String> getString(String key) {
        return Option.of(getString(key, null));
    }

    public Entry getEntry(int index) {
        return ObjectUtils.cast(get(index));
    }

    public Entry getEntry(String key) {
        return ObjectUtils.cast(get(key));
    }

    public Section getSection(int index) {
        return ObjectUtils.cast(get(index));
    }

    public Section getSection(String key) {
        return ObjectUtils.cast(get(key));
    }

    public String[] getOperators() {
        return operators;
    }

}
