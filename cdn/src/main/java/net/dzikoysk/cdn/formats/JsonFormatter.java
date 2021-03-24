package net.dzikoysk.cdn.formats;

import net.dzikoysk.cdn.CdnConstants;
import org.panda_lang.utilities.commons.CharacterUtils;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.Stack;

public final class JsonFormatter {

    private final String source;

    public JsonFormatter(String source) {
        this.source = source;
    }

    public String convertJsonToCdn() {
        return enforceNewlines();
    }

    // TODO: Simplify
    private String enforceNewlines() {
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        Stack<Character> sequence = new Stack<>();

        for (char character : source.replace("\r\n", CdnConstants.LINE_SEPARATOR).toCharArray()) {
            if (CharacterUtils.belongsTo(character, '"', '\'')) {
               if (sequence.isEmpty()) {
                   sequence.push(character);
               }
               else if (sequence.peek() == character) {
                   sequence.pop();
               }
            }

            if (sequence.isEmpty()) {
                if (CharacterUtils.belongsTo(character, '{', '[')) {
                    operators.push(character);
                    result.append(character);
                    appendNewline(result, operators);
                    continue;
                }

                if (character == ':') {
                    result.append(character).append(' ');
                    continue;
                }


                if (character == ',') {
                    result.append(character);
                    appendNewline(result, operators);
                    continue;
                }

                if (CharacterUtils.belongsTo(character, '}', ']')) {
                    operators.pop();
                    appendNewline(result, operators);
                }
                else if (CharacterUtils.belongsTo(character, ' ', '\n')) {
                    if (result.charAt(result.length() - 1) == character) {
                        continue;
                    }

                    if (character == '\n') {
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
        if (builder.charAt(builder.length() - 1) != '\n') {
            builder.append('\n').append(StringUtils.buildSpace(indentation.size() * 2));
        }
    }

}
