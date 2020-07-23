package net.dzikoysk.cdn.model;

import java.util.List;

public interface CdnElement<T> {

    T getValue();

    List<? extends String> getDescription();

    String getName();

}
