package net.dzikoysk.cdn.model;

import java.util.List;

public interface Element<T> {

    List<? extends String> getDescription();

    T getValue();

}
