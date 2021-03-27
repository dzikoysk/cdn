package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnUtils;

import java.util.List;

abstract class AbstractNamedElement<T> implements NamedElement<T> {

    protected final List<? extends String> description;
    protected final String name;
    protected final T value;

    protected AbstractNamedElement(List<? extends String> description, String name, T value) {
        this.description = description;
        this.name = name;
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
