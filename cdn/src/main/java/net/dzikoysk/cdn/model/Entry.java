package net.dzikoysk.cdn.model;

import java.util.List;

public class Entry extends AbstractNamedElement<Unit> {

    public Entry(List<? extends String> description, String name, Unit value) {
        super(description, name, value);
    }

    public Entry(List<? extends String> description, String name, String value) {
        this(description, name, new Unit(value));
    }

    public String getRecord() {
        return name + ": " + getUnitValue();
    }

    public String getUnitValue() {
        return getValue().getValue();
    }

}
