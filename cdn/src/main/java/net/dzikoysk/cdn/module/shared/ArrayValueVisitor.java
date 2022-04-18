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

package net.dzikoysk.cdn.module.shared;

import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Piece;

public class ArrayValueVisitor {

    private final String prefix;
    private final String suffix;
    private final boolean enforceQuotes;

    public ArrayValueVisitor(String prefix, String suffix, boolean enforceQuotes) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.enforceQuotes = enforceQuotes;
    }

    public Element<?> visit(Element<?> element) {
        if (element instanceof Piece) {
            Piece piece = (Piece) element;
            element = new Piece(prefix + CdnUtils.stringify(enforceQuotes, piece.getValue()) + suffix);
        }
        else if (element instanceof Entry) {
            Entry entry = (Entry) element;
            element = new Entry(element.getDescription(), prefix + entry.getName() + suffix, entry.getValue());
        }
        else if (element instanceof Section) {
            Section sectionElement = (Section) element;
            element = new Section(sectionElement.getDescription(), sectionElement.getOperators(), prefix + sectionElement.getName() + suffix, sectionElement.getValue());
        }
        else {
            throw new UnsupportedOperationException("Unsupported array component: " + element);
        }

        return element;
    }

}
