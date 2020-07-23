package net.dzikoysk.cdn.model;

import java.util.List;

public final class Entry extends AbstractConfigurationElement<String> {

    public Entry(String name, List<String> comments, String value) {
        super(name, comments, value);
    }

}
