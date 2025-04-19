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

import net.dzikoysk.cdn.module.standard.StandardOperators;
import panda.utilities.StringUtils;

import java.util.*;

/**
 * Represents the smallest piece of information in configuration
 */
public final class Piece implements Element<String> {

    private static final Set<String> STRING_OPERATORS = new HashSet<>(Arrays.asList(StandardOperators.STRING_OPERATORS));

    private final String value;

    public Piece(String value) {
        this.value = value.trim();
    }

    public Entry toEntry(List<? extends String> description) {
        String openingSymbol = Character.toString(value.charAt(0));
        String[] elements;

        if (isStringOperator(openingSymbol)) {
            StringBuilder entryKey = new StringBuilder(openingSymbol);
            int readIndex = 1;

            for (; readIndex < value.length(); readIndex++) {
                String currentChar = Character.toString(value.charAt(readIndex));
                entryKey.append(currentChar);

                if (currentChar.equals(openingSymbol)) {
                    break;
                }
            }

            elements = new String[] {
                entryKey.toString(),
                StringUtils.split(value.substring(readIndex + 1), StandardOperators.OPERATOR)[1].trim()
            };
        } else {
            elements = StringUtils.splitFirst(value, StandardOperators.OPERATOR);
        }

        String entryKey = elements.length > 0
                ? elements[0].trim()
                : value;

        String entryValue = elements.length == 2
                ? elements[1].trim()
                : entryKey;

        if (entryValue.endsWith(StandardOperators.SEPARATOR)) {
            entryValue = entryValue.substring(0, entryValue.length() - 1);
        }

        return new Entry(description, value, entryKey, entryValue);
    }

    private boolean isStringOperator(String operator) {
        for (String stringOperator : StandardOperators.STRING_OPERATORS) {
            if (stringOperator.equals(operator)) {
                return true;
            }
        }
        return false;
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
