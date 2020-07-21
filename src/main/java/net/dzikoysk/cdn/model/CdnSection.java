package net.dzikoysk.cdn.model;

import java.util.List;
import java.util.Map;

public final class CdnSection extends AbstractCdnElement<Map<String, CdnElement<?>>> {

    public CdnSection(String name, Map<String, CdnElement<?>> value, List<String> comments) {
        super(name, value, comments);
    }

}
