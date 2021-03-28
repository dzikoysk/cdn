package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Represents the smallest piece of information in configuration
 */
public final class Unit implements Element<String> {

    private final String value;

    public Unit(String value) {
        this.value = value;
    }

    public Entry toEntry(List<? extends String> description) {
        String[] elements = StringUtils.splitFirst(value, CdnConstants.OPERATOR);

        String entryKey = elements.length > 0
                ? elements[0].trim()
                : value;

        String entryValue = elements.length == 2
                ? elements[1].trim()
                : entryKey;

        if (entryValue.endsWith(CdnConstants.SEPARATOR)) {
            entryValue = entryValue.substring(0, entryValue.length() - 1);
        }

        return new Entry(description, entryKey, entryValue);
    }

    @Override
    public String toString() {
        return "Unit { " + value + " }";
    }

    @Override
    public List<? extends String> getDescription() {
        return Collections.emptyList();
    }

    @Override
    public String getValue() {
        return value;
    }

}
