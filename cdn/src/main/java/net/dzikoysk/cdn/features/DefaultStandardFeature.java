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

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnFeature;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;

import java.util.Arrays;
import java.util.Stack;

/**
 * Default implementation of CDN file format
 */
public class DefaultStandardFeature implements CdnFeature {

    @Override
    public void visitDescription(StringBuilder output, String indentation, String description) {
        output.append(indentation)
                .append(description)
                .append(CdnConstants.LINE_SEPARATOR);
    }

    @Override
    public void visitSectionOpening(StringBuilder output, String indentation, Section section) {
        // Don't add space to unnamed sections
        // ~ https://github.com/dzikoysk/cdn/issues/29
        output.append(indentation)
                .append(section.getName())
                .append(section.getName().isEmpty() ? "" : " ").append(section.getOperators()[0])
                .append(CdnConstants.LINE_SEPARATOR);
    }

    @Override
    public void visitSectionEnding(StringBuilder output, String indentation, Section section) {
        output.append(indentation)
                .append(section.getOperators()[1])
                .append(CdnConstants.LINE_SEPARATOR);
    }

    @Override
    public boolean resolveArray(Stack<Section> sections, Unit value) {
        return !sections.isEmpty() && Arrays.equals(sections.peek().getOperators(), CdnConstants.ARRAY_SEPARATOR);
    }

}
