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
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;

import java.util.Stack;

import static net.dzikoysk.cdn.CdnConstants.ARRAY;

/**
 * Implementation of YAML-like format. Supported features:
 * <ul>
 *     <li>Indentation based formatting</li>
 *     <li>Colon operator after section names</li>
 *     <li>Dash operator before array entry</li>
 * </ul>
 */
public final class YamlFeature implements CdnFeature {

    @Override
    public String convertToCdn(String source) {
        YamlConverter converter = new YamlConverter(source);
        return converter.convert();
    }

    @Override
    public void visitSectionOpening(StringBuilder output, String indentation, Section section) {
        output.append(indentation)
                .append(section.getName()).append(":")
                .append(CdnConstants.LINE_SEPARATOR);
    }

    @Override
    public Element<?> visitArrayValue(Element<?> element) {
        if (element instanceof Unit) {
            Unit unit = (Unit) element;
            element = new Unit(ARRAY + " " + unit.getValue());
        }
        else if (element instanceof Entry) {
            Entry entry = (Entry) element;
            element = new Entry(element.getDescription(), ARRAY + " " + entry.getName(), entry.getValue());
        }
        else if (element instanceof Section) {
            Section sectionElement = (Section) element;
            element = new Section(sectionElement.getDescription(), sectionElement.getOperators(), ARRAY + " " + sectionElement.getName(), sectionElement.getValue());
        }
        else {
            throw new UnsupportedOperationException("Unsupported list component: " + element);
        }

        return element;
    }

    @Override
    public Element<?> resolveArrayValue(Element<?> element) {
        if (element instanceof Unit) {
            Unit unit = (Unit) element;
            element = new Unit(unit.getValue().replaceFirst(CdnConstants.ARRAY, "").trim());
        }
        else if (element instanceof Entry) {
            Entry entry = (Entry) element;
            element = new Entry(element.getDescription(), entry.getName().replaceFirst(CdnConstants.ARRAY, "").trim(), entry.getValue());
        }
        else if (element instanceof Section) {
            Section sectionElement = (Section) element;
            element = new Section(sectionElement.getDescription(), sectionElement.getOperators(), CdnConstants.ARRAY + " " + sectionElement.getName(), sectionElement.getValue());
        }
        else {
            throw new UnsupportedOperationException("Unsupported list component: " + element);
        }

        return element;
    }

    @Override
    public boolean resolveArray(Stack<Section> sections, Unit unit) {
        return unit.getValue().startsWith(ARRAY);
    }

}
