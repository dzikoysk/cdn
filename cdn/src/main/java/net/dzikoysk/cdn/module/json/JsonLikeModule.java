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

package net.dzikoysk.cdn.module.json;

import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.CdnModule;
import net.dzikoysk.cdn.module.standard.StandardModule;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.module.shared.ArrayValueVisitor;
import org.jetbrains.annotations.Nullable;
import panda.std.Blank;
import panda.std.Result;

import static net.dzikoysk.cdn.module.standard.StandardOperators.LINE_SEPARATOR;
import static net.dzikoysk.cdn.module.standard.StandardOperators.OPERATOR;
import static net.dzikoysk.cdn.module.standard.StandardOperators.SEPARATOR;
import static panda.std.Result.ok;

/**
 * Implementation of JSON file format based on default implementation of CDN format.
 */
public final class JsonLikeModule extends StandardModule {

    private static final ArrayValueVisitor ARRAY_VALUE_VISITOR = new ArrayValueVisitor("", "");

    @Override
    public String convertToCdn(String source) {
        String standardized = source.replace("\r\n", LINE_SEPARATOR);
        return new JsonToCdnConverter().enforceNewlines(standardized);
    }

    @Override
    public Element<?> visitArrayValue(Element<?> element) {
        return ARRAY_VALUE_VISITOR.visit(element);
    }

    @Override
    public void renderDescription(StringBuilder output, String indentation, String description) {
        // drop comments
    }

    @Override
    public void renderSectionOpening(StringBuilder output, String indentation, Section section) {
        output.append(indentation)
                .append(CdnUtils.forceStringify(section.getName()))
                .append(": ")
                .append(section.getOperators()[0])
                .append(LINE_SEPARATOR);
    }

    @Override
    public void renderSectionEnding(StringBuilder output, String indentation, @Nullable Section parent, Section section) {
        output.append(indentation).append(section.getOperators()[1]);

        if (!CdnModule.isLastElementInSection(parent, section)) {
            output.append(SEPARATOR);
        }

        output.append(LINE_SEPARATOR);
    }

    @Override
    public Result<Blank, Exception> renderEntry(StringBuilder output, String indentation, @Nullable Section parent, Entry element) {
        output.append(indentation)
                .append(CdnUtils.forceStringify(element.getName()))
                .append(OPERATOR)
                .append(" ")
                .append(CdnUtils.forceStringify(element.getPieceValue()));

        if (!CdnModule.isLastElementInSection(parent, element)) {
            output.append(SEPARATOR);
        }

        output.append(LINE_SEPARATOR);
        return ok();
    }

    @Override
    public Result<Blank, Exception> renderPiece(StringBuilder output, String indentation, @Nullable Section parent, Piece element) {
        output.append(indentation).append(CdnUtils.forceStringify(element.getValue()));

        if (!CdnModule.isLastElementInSection(parent, element)) {
            output.append(SEPARATOR);
        }

        output.append(LINE_SEPARATOR);
        return ok();
    }

}
