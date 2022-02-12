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

package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Array;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.source.Source;
import panda.std.Blank;
import panda.std.Option;
import panda.std.Result;
import panda.utilities.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static panda.std.Blank.BLANK;
import static panda.std.Result.ok;

final class CdnReader {

    private final CdnSettings settings;
    private final Configuration root = new Configuration();
    private final Stack<Section> sections = new Stack<>();
    private List<String> description = new ArrayList<>();

    CdnReader(CdnSettings settings) {
        this.settings = settings;
    }

    public Result<Configuration, ? extends CdnException> read(Source sourceProvider) {
        String source = sourceProvider.getSource();
        source = settings.getModule().convertToCdn(source);

        // replace system-dependent line separators with unified one
        String normalizedSource = StringUtils.replace(source.trim(), System.lineSeparator(), StandardOperators.LINE_SEPARATOR);

        List<String> lines = Arrays.stream(normalizedSource.split(StandardOperators.LINE_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());

        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String originalLine = lines.get(lineNumber).trim();
            int currentLineNumber = lineNumber;

            Result<Blank, Exception> result = Result.attempt(() -> {
                String line = originalLine;

                // handle description
                if (line.isEmpty() || line.startsWith(StandardOperators.COMMENT_OPERATORS[0]) || line.startsWith(StandardOperators.COMMENT_OPERATORS[1])) {
                    description.add(line);
                    return BLANK;
                }

                // remove operator at the end of line
                if (line.endsWith(StandardOperators.SEPARATOR)) {
                    line = line.substring(0, line.length() - StandardOperators.SEPARATOR.length()).trim();
                }

                boolean isArray = line.endsWith(StandardOperators.ARRAY_SEPARATOR[0]);

                // initialize section
                if ((isArray || line.endsWith(StandardOperators.OBJECT_SEPARATOR[0]))) {
                    String sectionName = trimSeparator(line);

                    if (sectionName.endsWith(StandardOperators.OPERATOR)) {
                        sectionName = sectionName.substring(0, sectionName.length() - StandardOperators.OPERATOR.length()).trim();
                    }

                    Section section = isArray
                            ? new Array(description, sectionName)
                            : new Section(description, sectionName);

                    appendElement(section);
                    sections.push(section); // has to be after append

                    description = new ArrayList<>();
                    return BLANK;
                }
                // pop section
                else if (!sections.isEmpty() && line.endsWith(sections.peek().getOperators()[1])) {
                    String lineBefore = trimSeparator(line);

                    // skip values with section operators
                    if (lineBefore.isEmpty()) {
                        sections.pop();
                        return BLANK;
                    }
                }

                // add standard entry
                Piece piece = new Piece(originalLine);
                boolean isInArray = settings.getModule().resolveArray(sections, piece);
                appendElement(isInArray ? piece : piece.toEntry(description));
                description = new ArrayList<>();
                return BLANK;
            });

            if (result.isErr()) {
                return result
                        .mapErr(exception -> new CdnExceptionInSource("Cannot read sources", originalLine, currentLineNumber, exception))
                        .projectToError();
            }
        }

        // flat map json-like formats with declared root operators
        return ok(Option.when(root.size() == 1, root)
                .flatMap(root -> root.getSection(0))
                .filter(element -> element.getName().isEmpty())
                .map(element -> new Configuration(element.getValue()))
                .orElseGet(root));
    }

    private Element<?> appendElement(Element<?> element) {
        return sections.isEmpty() ? root.append(element) : sections.peek().append(element);
    }

    private String trimSeparator(String line) {
        return line.substring(0, line.length() - 1).trim();
    }

}
