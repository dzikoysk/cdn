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

    public Configuration(List<ConfigurationElement<?>> elements) {
        super(CdnConstants.OBJECT_SEPARATOR, StringUtils.EMPTY, Collections.emptyList(), elements);
    }

}
