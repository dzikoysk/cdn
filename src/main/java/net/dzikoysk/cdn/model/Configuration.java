package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.Collections;

public final class Configuration extends Section {

    public Configuration() {
        super(CdnConstants.OBJECT_SEPARATOR, StringUtils.EMPTY, Collections.emptyList());
    }

}
