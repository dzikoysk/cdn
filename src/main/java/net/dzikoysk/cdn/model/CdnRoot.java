package net.dzikoysk.cdn.model;

import org.panda_lang.utilities.commons.StringUtils;

import java.util.Collections;

public final class CdnRoot extends CdnSection {

    public CdnRoot() {
        super(StringUtils.EMPTY, Collections.emptyList());
    }

}
