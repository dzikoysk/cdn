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

package net.dzikoysk.cdn.module.json;

import panda.utilities.CharacterUtils;
import panda.utilities.StringUtils;
import java.util.Stack;

import static net.dzikoysk.cdn.module.json.JsonOperators.CLOSING_OPERATORS;
import static net.dzikoysk.cdn.module.json.JsonOperators.DELIMITER_OPERATOR;
import static net.dzikoysk.cdn.module.json.JsonOperators.ENTRY_OPERATOR;
import static net.dzikoysk.cdn.module.json.JsonOperators.LN;
import static net.dzikoysk.cdn.module.json.JsonOperators.OPENING_OPERATORS;
import static net.dzikoysk.cdn.module.json.JsonOperators.SEQUENCES;

final class JsonToCdnConverter {

    String enforceNewlines(String source) {
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        Stack<Character> sequence = new Stack<>();

        for (char character : source.toCharArray()) {
            // Handle sequences
            if (CharacterUtils.belongsTo(character, SEQUENCES)) {
                if (sequence.isEmpty()) {
                    sequence.push(character);
                }
                else if (sequence.peek() == character) {
                    sequence.pop();
                }
            }

            // Handle non-sequence operators
            if (sequence.isEmpty()) {
                if (CharacterUtils.belongsTo(character, OPENING_OPERATORS)) {
                    operators.push(character);
                    result.append(character);
                    appendNewline(result, operators);
                    continue;
                }

                if (character == ENTRY_OPERATOR) {
                    result.append(character).append(' ');
                    continue;
                }

                if (character == DELIMITER_OPERATOR) {
                    result.append(character);
                    appendNewline(result, operators);
                    continue;
                }

                if (CharacterUtils.belongsTo(character, CLOSING_OPERATORS)) {
                    operators.pop();
                    appendNewline(result, operators);
                }
                else if (CharacterUtils.belongsTo(character, ' ', LN)) {
                    if (lastChar(result) == character) {
                        continue;
                    }

                    if (character == LN) {
                        appendNewline(result, operators);
                        continue;
                    }
                }
            }

            result.append(character);
        }

        return result.toString();
    }

    private void appendNewline(StringBuilder builder, Stack<?> indentation) {
        if (lastChar(builder) != LN) {
            builder.append(LN).append(StringUtils.buildSpace(indentation.size() * 2));
        }
    }

    private char lastChar(StringBuilder builder) {
        return builder.charAt(builder.length() - 1);
    }

}
