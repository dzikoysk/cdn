package net.dzikoysk.cdn.features;

import org.panda_lang.utilities.commons.StringUtils;

import static net.dzikoysk.cdn.CdnConstants.*;

final class YamlConverter {

    private final String[] lines;
    private final StringBuilder converted = new StringBuilder();
    private int previousIndentation = 0;

    YamlConverter(String source) {
        this.lines = StringUtils.split(source.replace(System.lineSeparator(), LINE_SEPARATOR), LINE_SEPARATOR);
    }

    String convert() {
        for (String line : lines) {
            String indentation = StringUtils.extractParagraph(line);
            line = line.trim();
            close(indentation.length());

            if (line.endsWith(OPERATOR)) {
                converted.append(indentation)
                        .append(line, 0, line.length() - 1)
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
