/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.model;

import net.dzikoysk.cdn.CdnConstants;
import panda.utilities.StringUtils;
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

        return new Entry(description, value, entryKey, entryValue);
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
