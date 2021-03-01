package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnUtils;
import org.jetbrains.annotations.Contract;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.util.ArrayList;
import java.util.List;

public class Section extends AbstractConfigurationElement<List<? extends ConfigurationElement<?>>> {

    private final String[] operators;

    public Section(String name, List<? extends String> description) {
        this(CdnConstants.OBJECT_SEPARATOR, name, description);
    }

    public Section(String[] operators, String name, List<? extends String> description) {
        this(operators, name, description, new ArrayList<>());
    }

    public Section(String name, List<? extends String> description, List<? extends ConfigurationElement<?>> value) {
        this(CdnConstants.OBJECT_SEPARATOR, name, description, value);
    }

    public Section(String[] operators, String name, List<? extends String> description, List<? extends ConfigurationElement<?>> value) {
        super(name, description, value);
        this.operators = operators;
    }

    public <E extends ConfigurationElement<?>> E append(E element) {
        super.value.add(ObjectUtils.cast(element));
        return element;
    }

    @Contract("null -> false")
    public boolean has(String key) {
        return get(key).isDefined();
    }

    public int size() {
        return getValue().size();
    }

    private <T> Option<T> get(int index, Class<T> type) {
        return get(index).map(element -> ObjectUtils.cast(type, element));
    }

    public Option<ConfigurationElement<?>> get(int index) {
        return Option.when(index > -1 && index < size(), () -> getValue().get(index));
    }

    private <T> Option<T> get(String key, Class<T> type) {
        return get(key).map(element -> {
            T value = ObjectUtils.cast(type, element);

            if (value == null) {
                throw new IllegalStateException("Property '" + key + "' of type " + element.getClass() + " cannot be queried as " + type);
            }

            return value;
        });
    }

    @Contract("null -> null")
    public Option<ConfigurationElement<?>> get(String key) {
        if (key == null) {
            return Option.none();
        }

        for (ConfigurationElement<?> element : getValue()) {
            if (key.equals(element.getName())) {
                return Option.of(element);
            }
        }

        return Option.when(key.contains("."), StringUtils.split(key, "."))
                .flatMap(qualifier -> {
                    Option<Section> section = Option.of(this);

                    for (int index = 0; index < qualifier.length - 1 && section.isDefined(); index++) {
                        int currentIndex = index;
                        section = section.flatMap(value -> value.getSection(qualifier[currentIndex]));
                    }

                    return section.flatMap(value -> value.get(qualifier[qualifier.length - 1]));
                });
    }

    @Contract("_, null -> null")
    public List<String> getList(String key, List<String> defaultValue) {
        return getList(key).orElseGet(() -> defaultValue);
    }

    public Option<List<String>> getList(String key) {
        return getSection(key).map(Section::getList);
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
        return getString(key).map(Boolean::parseBoolean);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElseGet(defaultValue);
    }

    public Option<Integer> getInt(String key) {
        return getString(key).map(Integer::parseInt);
    }

    public int getInt(String key, int defaultValue) {
        return getInt(key).orElseGet(defaultValue);
    }

    public Option<String> getString(String key) {
        return getEntry(key)
                .map(Entry::getValue)
                .map(CdnUtils::destringify);
    }

    @Contract("_, null -> null")
    public String getString(String key, String defaultValue) {
        return getString(key).orElseGet(defaultValue);
    }

    public Option<Entry> getEntry(int index) {
        return get(index, Entry.class);
    }

    public Option<Entry> getEntry(String key) {
        return get(key, Entry.class);
    }

    public Option<Section> getSection(int index) {
        return get(index, Section.class);
    }

    public Option<Section> getSection(String key) {
        return get(key, Section.class);
    }

    public String[] getOperators() {
        return operators;
    }

}
