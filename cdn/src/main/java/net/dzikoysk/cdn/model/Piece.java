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

import java.util.stream.Collectors;
import net.dzikoysk.cdn.module.standard.StandardOperators;

import java.util.*;

/**
 * Represents the smallest piece of information in configuration
 */
public final class Piece implements Element<String> {

    private static final Set<Character> STRING_OPERATORS = Arrays.stream(StandardOperators.STRING_OPERATORS)
        .map(s -> s.charAt(0))
        .collect(Collectors.toSet());

    private final String value;

    public Piece(String value) {
        this.value = value.trim();
    }

    public Entry toEntry(List<String> description) {
        char operator = value.charAt(0);
        return STRING_OPERATORS.contains(operator)
            ? parseWithOperator(operator, description)
            : parseWithoutOperator(description);
    }

    private Entry parseWithOperator(char operator, List<String> description) {
        StringBuilder key = new StringBuilder().append(operator);
        int readIndex = 1;

        while (readIndex < value.length()) {
            char currentChar = value.charAt(readIndex++);
            key.append(currentChar);

            if (currentChar == operator) {
                break;
            }
        }

        return new Entry(description, value, key.toString(), parseValue(value, readIndex));
    }

    private Entry parseWithoutOperator(List<String> description) {
        int separatorIndex = value.indexOf(StandardOperators.OPERATOR);
        if (separatorIndex == -1) {
            return new Entry(description, value, value, value);
        }

        String key = value.substring(0, separatorIndex).trim();
        return new Entry(description, value, key, parseValue(value, separatorIndex));
    }

    private static String parseValue(String raw, int operatorIndex) {
        String value = raw.substring(operatorIndex + StandardOperators.OPERATOR.length());
        return value.endsWith(StandardOperators.SEPARATOR)
            ? value.substring(0, value.length() - StandardOperators.SEPARATOR.length()).trim()
            : value.trim();
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
