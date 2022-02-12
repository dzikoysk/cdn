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
import net.dzikoysk.cdn.module.CdnModule;
import org.jetbrains.annotations.Nullable;
import panda.std.Blank;
import panda.std.Result;
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

        return render(content, 0, null, element)
                .map(success -> {
                    String result = content.toString();

                    for (Map.Entry<? extends String, ? extends String> entry : settings.getPlaceholders().entrySet()) {
                        result = StringUtils.replace(result, "${{" + entry.getKey() + "}}", entry.getValue());
                    }

                    return result.trim();
                })
                .mapErr(CdnException::new);
    }

    private Result<Blank, Exception> render(StringBuilder output, int level, @Nullable Section parent, Element<?> element) {
        String indentation = StringUtils.buildSpace(level * 2);
        CdnModule module = settings.getModule();

        // render multiline description
        for (String description : element.getDescription()) {
            module.renderDescription(output, indentation, description);
        }

        // render simple entry
        if (element instanceof Entry) {
            return module.renderEntry(output, indentation, parent, (Entry) element);
        }

        // render section
        if (element instanceof Section) {
            Section section = (Section) element;
            boolean isRoot = section instanceof Configuration;

            if (!isRoot) {
                module.renderSectionOpening(output, indentation, section);
            }

            // do not indent root sections
            int subLevel = isRoot
                    ? level
                    : level + 1;

            // render section content
            for (Element<?> sectionElement : section.getValue()) {
                render(output, subLevel, section, sectionElement);
            }

            // append opening operator for cdn format
            if (!isRoot) {
                module.renderSectionEnding(output, indentation, parent, section);
            }

            return ok();
        }

        if (element instanceof Piece) {
            return module.renderPiece(output, indentation, parent, (Piece) element);
        }

        return error(new IllegalStateException("Unknown element: " + element));
    }

}
