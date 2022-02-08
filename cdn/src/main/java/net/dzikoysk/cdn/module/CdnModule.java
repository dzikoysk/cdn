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

package net.dzikoysk.cdn.module;

import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Piece;
import org.jetbrains.annotations.Nullable;
import panda.std.Blank;
import panda.std.Result;

import java.util.Stack;

import static panda.std.Result.ok;

public interface CdnModule {

    default String convertToCdn(String source) { return source; }

    default Element<?> visitArrayValue(Element<?> element) { return element; }

    default Element<?> resolveArrayValue(Element<?> element) { return element; }

    default boolean resolveArray(Stack<Section> sections, Piece value) { return false; }

    default void renderDescription(StringBuilder output, String indentation, String description) { }

    default void renderSectionOpening(StringBuilder output, String indentation, Section section) { }

    default void renderSectionEnding(StringBuilder output, String indentation, @Nullable Section parent, Section section) { }

    default Result<Blank, Exception> renderEntry(StringBuilder output, String indentation, @Nullable Section parent, Entry element) { return ok(); }

    default Result<Blank, Exception> renderPiece(StringBuilder output, String indentation, @Nullable Section parent, Piece element) { return ok(); }

    static boolean isLastElementInSection(@Nullable Section parent, Element<?> element) {
        return parent == null || parent.getValue().indexOf(element) == parent.getValue().size() - 1;
    }

}
