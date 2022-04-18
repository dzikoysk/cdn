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

package net.dzikoysk.cdn.module.yaml;

import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Piece;
import net.dzikoysk.cdn.module.shared.ArrayValueVisitor;
import net.dzikoysk.cdn.module.standard.StandardModule;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import org.jetbrains.annotations.Nullable;
import panda.std.Blank;
import panda.std.Result;
import panda.utilities.StringUtils;
import java.util.Stack;
import java.util.regex.Pattern;

import static net.dzikoysk.cdn.module.standard.StandardOperators.ARRAY;
import static panda.std.Result.ok;

/**
 * Implementation of YAML-like format. Supported features:
 * <ul>
 *     <li>Indentation based formatting</li>
 *     <li>Colon operator after section names</li>
 *     <li>Dash operator before array entry</li>
 * </ul>
 */
public final class YamlLikeModule extends StandardModule {

    private static final Pattern DEFAULT_COMMENT_OPENING = Pattern.compile("//");

    private final boolean enforceQuotes;
    private final ArrayValueVisitor arrayValueVisitor;

    public YamlLikeModule(boolean enforceQuotes) {
        this.enforceQuotes = enforceQuotes;
        this.arrayValueVisitor = new ArrayValueVisitor(ARRAY + " ", "", enforceQuotes);
    }

    @Override
    public String convertToCdn(String source) {
        YamlLikeConverter converter = new YamlLikeConverter(source);
        return converter.convert();
    }

    @Override
    public void renderDescription(StringBuilder output, String indentation, String description) {
        description = StringUtils.trimStart(description);

        if (description.startsWith("//")) {
            description = DEFAULT_COMMENT_OPENING.matcher(description).replaceFirst("#");
        }

        output.append(indentation)
                .append(description)
                .append(StandardOperators.LINE_SEPARATOR);
    }

    @Override
    public void renderSectionOpening(StringBuilder output, String indentation, Section section) {
        output.append(indentation)
                .append(section.getName()).append(":")
                .append(StandardOperators.LINE_SEPARATOR);
    }

    @Override
    public void renderSectionEnding(StringBuilder output, String indentation, @Nullable Section parent, Section section) {
        // skip section operators in yaml-like format
    }

    @Override
    public Result<Blank, Exception> renderEntry(StringBuilder output, String indentation, @Nullable Section parent, Entry element) {
        output.append(indentation)
                .append(element.getName())
                .append(": ")
                .append(element.getPieceValue().trim().equals("[]") ? "[]" : CdnUtils.stringify(enforceQuotes, element.getPieceValue()))
                .append(StandardOperators.LINE_SEPARATOR);
        return ok();
    }

    @Override
    public Element<?> visitArrayValue(Element<?> element) {
        return arrayValueVisitor.visit(element);
    }

    @Override
    public Element<?> resolveArrayValue(Element<?> element) {
        if (element instanceof Piece) {
            Piece piece = (Piece) element;
            element = new Piece(piece.getValue().replaceFirst(StandardOperators.ARRAY, "").trim());
        }
        else if (element instanceof Entry) {
            Entry entry = (Entry) element;
            element = new Piece(entry.getRecord().replaceFirst(StandardOperators.ARRAY, ""));
        }
        else if (element instanceof Section) {
            Section sectionElement = (Section) element;
            element = new Section(sectionElement.getDescription(), sectionElement.getOperators(), StandardOperators.ARRAY + " " + sectionElement.getName(), sectionElement.getValue());
        }
        else {
            throw new UnsupportedOperationException("Unsupported list component: " + element);
        }

        return element;
    }

    @Override
    public boolean resolveArray(Stack<Section> sections, Piece piece) {
        return piece.getValue().startsWith(ARRAY);
    }

}
