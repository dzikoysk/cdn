package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.List;

public final class Entry extends AbstractConfigurationElement<String> {

    private final String record;

    private Entry(String record, String name, List<String> description, String value) {
        super(name, description, value);
        this.record = record;
    }

    public String getRecord() {
        return record;
    }

    public static Entry of(String record, List<String> description) {
        String[] elements = StringUtils.splitFirst(record, CdnConstants.OPERATOR);
        String key = elements.length > 0 ? elements[0].trim() : record;
        String value = elements.length == 2 ? elements[1].trim() : key;

        return new Entry(record, key, description, value);
    }

}
