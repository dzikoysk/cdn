package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Configuration extends Section {

    public Configuration() {
        this(new ArrayList<>());
    }

    public Configuration(List<? extends Element<?>> elements) {
        super(Collections.emptyList(), CdnConstants.OBJECT_SEPARATOR, StringUtils.EMPTY, elements);
    }

}
