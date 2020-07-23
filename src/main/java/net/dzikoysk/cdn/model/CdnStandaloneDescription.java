package net.dzikoysk.cdn.model;

import java.util.List;

final class CdnStandaloneDescription extends AbstractCdnElement<Object> {

    protected CdnStandaloneDescription(String name, List<String> comments, Object value) {
        super(name, comments, value);
    }

}
