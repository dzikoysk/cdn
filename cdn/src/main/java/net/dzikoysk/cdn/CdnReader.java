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
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

final class CdnReader {

    private final CdnSettings settings;
    private final Configuration root = new Configuration();
    private final Stack<Section> sections = new Stack<>();
    private List<String> description = new ArrayList<>();

    CdnReader(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration read(String source) {
        for (CdnFeature feature : settings.getFeatures()) {
            source = feature.convertToCdn(source);
        }

        // replace system-dependent line separators with unified one
        String normalizedSource = StringUtils.replace(source.trim(), System.lineSeparator(), CdnConstants.LINE_SEPARATOR);

        List<String> lines = Arrays.stream(normalizedSource.split(CdnConstants.LINE_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());

        for (String line : lines) {
            line = line.trim();
            String originalLine = line;

            // handle description
            if (line.isEmpty() || line.startsWith(CdnConstants.COMMENT_OPERATORS[0]) || line.startsWith(CdnConstants.COMMENT_OPERATORS[1])) {
                description.add(line);
                continue;
            }

            // remove operator at the end of line
            if (line.endsWith(CdnConstants.SEPARATOR)) {
                line = line.substring(0, line.length() - CdnConstants.SEPARATOR.length()).trim();
            }

            boolean isArray = line.endsWith(CdnConstants.ARRAY_SEPARATOR[0]);

            // initialize section
            if ((isArray || line.endsWith(CdnConstants.OBJECT_SEPARATOR[0]))) {
                String sectionName = trimSeparator(line);

                if (sectionName.endsWith(CdnConstants.OPERATOR)) {
                    sectionName = sectionName.substring(0, sectionName.length() - CdnConstants.OPERATOR.length()).trim();
                }

                Section section = isArray
                        ? new Array(description, sectionName)
                        : new Section(description, sectionName);

                appendElement(section);
                sections.push(section); // has to be after append

                description = new ArrayList<>();
                continue;
            }
            // pop section
            else if (!sections.isEmpty() && line.endsWith(sections.peek().getOperators()[1])) {
                String lineBefore = trimSeparator(line);

                // skip values with section operators
                if (lineBefore.isEmpty()) {
                    sections.pop();
                    continue;
                }
            }

            // add standard entry
            Unit unit = new Unit(originalLine);
            boolean isInArray = false;

            for (CdnFeature feature : settings.getFeatures()) {
                if (isInArray = feature.resolveArray(sections, unit)) {
                    break;
                }
            }

            appendElement(isInArray ? unit : unit.toEntry(description));
            description = new ArrayList<>();
        }

        // flat map json-like formats with declared root operators
        return Option.when(root.size() == 1, root)
                .flatMap(root -> root.getSection(0))
                .filter(element -> element.getName().isEmpty())
                .map(element -> new Configuration(element.getValue()))
                .orElseGet(root);
    }

    private Element<?> appendElement(Element<?> element) {
        return sections.isEmpty() ? root.append(element) : sections.peek().append(element);
    }

    private String trimSeparator(String line) {
        return line.substring(0, line.length() - 1).trim();
    }

}
