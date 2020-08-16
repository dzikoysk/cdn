package net.dzikoysk.cdn;

import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.text.ContentJoiner;

import static net.dzikoysk.cdn.CdnConstants.*;

final class CdnPrettier {

    private final String[] lines;
    private final StringBuilder converted = new StringBuilder();
    private int previousIndentation = 0;

    CdnPrettier(String source) {
        this.lines = StringUtils.split(source.replace(System.lineSeparator(), LINE_SEPARATOR), LINE_SEPARATOR);
    }

    String tryToInsertNewLinesInADumbWay() {
        return ContentJoiner.on("").join(lines, String::trim).toString()
                .replace(OBJECT_SEPARATOR[0], OBJECT_SEPARATOR[0] + LINE_SEPARATOR)
                .replace(OBJECT_SEPARATOR[1], LINE_SEPARATOR + OBJECT_SEPARATOR[1] + LINE_SEPARATOR)
                .replace(ARRAY_SEPARATOR[0], ARRAY_SEPARATOR[0] + LINE_SEPARATOR)
                .replace(ARRAY_SEPARATOR[1], LINE_SEPARATOR + ARRAY_SEPARATOR[1] + LINE_SEPARATOR)
                .replace(SEPARATOR, SEPARATOR + LINE_SEPARATOR)
                // Cleanup
                // -------
                // remove doubled line separators for neighbouring sections
                .replace(LINE_SEPARATOR + LINE_SEPARATOR, LINE_SEPARATOR)
                .replace(LINE_SEPARATOR + ' ' + LINE_SEPARATOR, LINE_SEPARATOR)
                // remove line separators between separated sections
                .replace(LINE_SEPARATOR + SEPARATOR + LINE_SEPARATOR, SEPARATOR + LINE_SEPARATOR)
                .trim();
    }

    String tryToConvertIndentationInADumbWay() {
        for (String line : lines) {
            String indentation = StringUtils.extractParagraph(line);
            line = line.trim();
            close(indentation.length());

            if (line.endsWith(OPERATOR)) {
                converted.append(indentation)
                        .append(line)
                        .append(" {")
                        .append(LINE_SEPARATOR);
            }
            else {
                converted.append(indentation)
                        .append(line)
                        .append(LINE_SEPARATOR);
            }

            previousIndentation = indentation.length();
        }

        close(0);
        return converted.toString();
    }

    private void close(int toIndentation) {
        while (previousIndentation > toIndentation) {
            previousIndentation = previousIndentation - 2;

            converted.append(StringUtils.buildSpace(previousIndentation))
                    .append("}")
                    .append(LINE_SEPARATOR);
        }
    }

}
