package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnSection;

public final class CdnParser {

    public CdnSection parse(String source) {
        return new CdnReader().read(source);
    }

    public String compose(CdnElement<?> element) {
        return new CdnWriter().render(element);
    }

}
