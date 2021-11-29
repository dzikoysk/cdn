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

import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import panda.std.Result;
import panda.std.Unit;
import panda.utilities.StringUtils;
import java.util.Map;

import static panda.std.Result.error;
import static panda.std.Result.ok;

final class CdnWriter {

    private final CdnSettings settings;

    CdnWriter(CdnSettings settings) {
        this.settings = settings;
    }

    public Result<String, CdnException> render(Element<?> element) {
        StringBuilder content = new StringBuilder();

        return render(content, 0, element)
                .map(success -> {
                    String result = content.toString();

                    for (Map.Entry<? extends String, ? extends String> entry : settings.getPlaceholders().entrySet()) {
                        result = StringUtils.replace(result, "${{" + entry.getKey() + "}}", entry.getValue());
                    }

                    return result.trim();
                })
                .mapErr(CdnException::new);
    }

    private Result<Unit, Exception> render(StringBuilder output, int level, Element<?> element) {
        String indentation = StringUtils.buildSpace(level * 2);

        // render multiline description
        for (String description : element.getDescription()) {
            settings.getModules().visitDescription(output, indentation, description);
        }

        // render simple entry
        if (element instanceof Entry) {
            output.append(indentation)
                    .append(((Entry) element).getRecord())
                    .append(StandardOperators.LINE_SEPARATOR);
            return ok();
        }

        // render section
        if (element instanceof Section) {
            Section section = (Section) element;
            boolean isRoot = section instanceof Configuration;

            if (!isRoot) {
                settings.getModules().visitSectionOpening(output, indentation, section);
            }

            // do not indent root sections
            int subLevel = isRoot
                    ? level
                    : level + 1;

            // render section content
            for (Element<?> sectionElement : section.getValue()) {
                render(output, subLevel, sectionElement);
            }

            // append opening operator for cdn format
            if (!isRoot) {
                settings.getModules().visitSectionEnding(output, indentation, section);
            }

            return ok();
        }

        if (element instanceof Piece) {
            output.append(indentation)
                    .append(((Piece) element).getValue())
                    .append(StandardOperators.LINE_SEPARATOR);
            return ok();
        }

        return error(new IllegalStateException("Unknown element: " + element));
    }

}
