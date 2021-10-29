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

import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;

import java.util.Stack;

public interface CdnFeature {

    default String convertToCdn(String source) { return source; }

    default void visitDescription(StringBuilder output, String indentation, String description) { }

    default void visitSectionOpening(StringBuilder output, String indentation, Section section) { }

    default void visitSectionEnding(StringBuilder output, String indentation, Section section) { }

    default Element<?> visitArrayValue(Element<?> element) { return element; }

    default Element<?> resolveArrayValue(Element<?> element) { return element; }

    default boolean resolveArray(Stack<Section> sections, Unit value) { return false; }

}
