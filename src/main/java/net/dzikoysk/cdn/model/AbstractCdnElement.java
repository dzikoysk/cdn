package net.dzikoysk.cdn.model;

import java.util.List;

abstract class AbstractCdnElement<T> implements CdnElement<T>  {

    protected final String name;
    protected final T value;
    protected final List<String> comments;

    protected AbstractCdnElement(String name, T value, List<String> comments) {
        this.name = name;
        this.value = value;
        this.comments = comments;
    }

    @Override
    public List<? extends String> getDescription() {
        return comments;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

}
