package net.dzikoysk.cdn.model;

import java.util.List;
import java.util.UUID;

public final class StandaloneDescription extends AbstractConfigurationElement<List<? extends String>> {

    protected StandaloneDescription(List<? extends String> description) {
        super(UUID.randomUUID().toString(), description, description);
    }

}
