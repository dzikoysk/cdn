package net.dzikoysk.cdn.converters;

import net.dzikoysk.cdn.CdnConverter;
import org.panda_lang.utilities.commons.StringUtils;

import static net.dzikoysk.cdn.CdnConstants.*;

public final class YamlConverter implements CdnConverter {

    @Override
    public String convertToCdn(String source) {
        Converter converter = new Converter(source);
        return converter.convert(source);
    }

    private static final class Converter {

        private final String[] lines;
        private final StringBuilder converted = new StringBuilder();
        private int previousIndentation = 0;

        private Converter(String source) {
            this.lines = StringUtils.split(source.replace(System.lineSeparator(), LINE_SEPARATOR), LINE_SEPARATOR);
        }

        private String convert(String source) {
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

}
