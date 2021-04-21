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

package net.dzikoysk.cdn.features;

import org.panda_lang.utilities.commons.StringUtils;

import static net.dzikoysk.cdn.CdnConstants.*;

final class YamlLikeConverter {

    private final String[] lines;
    private final StringBuilder converted = new StringBuilder();
    private int previousIndentation = 0;

    YamlLikeConverter(String source) {
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
