package net.dzikoysk.cdn.model;

import java.util.List;

public interface ConfigurationElement<T> {

    T getValue();

    List<? extends String> getDescription();

    String getName();

}
