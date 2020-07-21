package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnRoot;

public final class CDN {

    private CDN() { }

    public static CdnRoot parse(String source) {
        return new CdnReader().read(source);
    }

    public static String compose(CdnElement<?> element) {
        return new CdnWriter().render(element);
    }

}
