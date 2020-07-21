package net.dzikoysk.cdn.model;

import java.util.List;

public final class CdnEntry extends AbstractCdnElement<String> {

    public CdnEntry(String name, List<String> comments, String value) {
        super(name, value, comments);
    }

}
