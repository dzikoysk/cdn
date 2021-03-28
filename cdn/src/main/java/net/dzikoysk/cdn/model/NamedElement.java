package net.dzikoysk.cdn.model;

/**
 * Represents standalone configuration elements.
 * Standalone elements require name to support configuration querying.
 *
 * @param <T> type of stored value
 */
public interface NamedElement<T> extends Element<T> {

    /**
     * The name of element
     *
     * @return
     */
    String getName();

}
