package net.dzikoysk.cdn.model;

import java.util.List;

public final class StandaloneDescription extends AbstractConfigurationElement<Object> {

    protected StandaloneDescription(String name, List<String> comments, Object value) {
        super(name, comments, value);
    }

}
