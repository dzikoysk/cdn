package net.dzikoysk.cdn.model;

import org.panda_lang.utilities.commons.StringUtils;

import java.util.Collections;
import java.util.Map;

public final class CdnRoot extends CdnSection {

    public CdnRoot(Map<String, CdnElement<?>> values) {
        super(StringUtils.EMPTY, Collections.emptyList(), values);
    }

}
