package net.dzikoysk.cdn.model;

import java.util.List;

public final class Unit extends AbstractConfigurationElement<String> {

    public Unit(String value, List<? extends String> description) {
        super(value, description, value);
    }

}
