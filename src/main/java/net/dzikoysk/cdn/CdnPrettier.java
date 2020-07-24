package net.dzikoysk.cdn;

import org.panda_lang.utilities.commons.StringUtils;

final class CdnPrettier {

    private final String[] lines;
    private final StringBuilder converted = new StringBuilder();
    private int previousIndentation = 0;

    CdnPrettier(String source) {
        this.lines = StringUtils.split(source.replace(System.lineSeparator(), CdnConstants.LINE_SEPARATOR), CdnConstants.LINE_SEPARATOR);
    }

    String tryToConvertIndentationInADumbWay() {
        for (String line : lines) {
            String indentation = StringUtils.extractParagraph(line);
            line = line.trim();
            close(indentation.length());

            if (line.endsWith(":")) {
                converted.append(indentation)
                        .append(line)
                        .append(" {")
                        .append(CdnConstants.LINE_SEPARATOR);
            }
            else {
                converted.append(indentation)
                        .append(line)
                        .append(CdnConstants.LINE_SEPARATOR);
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
                    .append(CdnConstants.LINE_SEPARATOR);
        }
    }

}
