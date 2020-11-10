package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnUtils;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.List;

public final class Entry extends AbstractConfigurationElement<String> {

    private final String record;

    private Entry(String record, String name, List<? extends String> description, String value) {
        super(name, description, value);
        this.record = record;
    }

    @Override
    public String getValue() {
        return CdnUtils.destringify(super.getValue());
    }

    public String getRecord() {
        return record;
    }

    public static Entry ofPair(String key, Object value, List<? extends String> description) {
        return of(key + ": " + value, description);
    }

    public static Entry of(String record, List<? extends String> description) {
        String[] elements = StringUtils.splitFirst(record, CdnConstants.OPERATOR);
        String key = elements.length > 0 ? elements[0].trim() : record;
        String value = elements.length == 2 ? elements[1].trim() : key;

        if (value.endsWith(CdnConstants.SEPARATOR)) {
            value = value.substring(0, value.length() - 1);
        }

        return new Entry(record, key, description, value);
    }

}
