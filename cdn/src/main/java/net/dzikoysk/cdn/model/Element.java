package net.dzikoysk.cdn.model;

import java.util.List;

/**
 * Represents the simplest configuration element.
 *
 * @param <T> type of stored value
 */
public interface Element<T> {

    List<? extends String> getDescription();

    T getValue();

}
