package net.dzikoysk.cdn.model;

import java.util.List;

public final class CdnEntry extends AbstractCdnElement<String> {

    public CdnEntry(String name, String value, List<String> comments) {
        super(name, value, comments);
    }

}
