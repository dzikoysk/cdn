package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnUtils;

import java.util.List;

abstract class AbstractConfigurationElement<T> implements ConfigurationElement<T> {

    protected final String name;
    protected final List<String> description;
    protected final T value;

    protected AbstractConfigurationElement(String name, List<String> description,  T value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    @Override
    public List<? extends String> getDescription() {
        return description;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getName() {
        return CdnUtils.destringify(name);
    }

}
