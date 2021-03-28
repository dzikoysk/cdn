package net.dzikoysk.cdn.model;

import java.util.List;

/**
 * Represents key-value relation of {@link java.lang.String} and {@link net.dzikoysk.cdn.model.Unit}
 */
public class Entry extends AbstractNamedElement<Unit> {

    public Entry(List<? extends String> description, String name, Unit value) {
        super(description, name, value);
    }

    public Entry(List<? extends String> description, String name, String value) {
        this(description, name, new Unit(value));
    }

    /**
     * Get entry as 'key: value' text.
     *
     * @return entry as string
     */
    public String getRecord() {
        return name + ": " + getUnitValue();
    }

    /**
     * Get unit's value using {@link net.dzikoysk.cdn.model.Unit#getValue()} method.
     *
     * @return the unit's value
     */
    public String getUnitValue() {
        return getValue().getValue();
    }

    @Override
    public String toString() {
        return "Entry { " + getRecord() + " }";
    }

}
