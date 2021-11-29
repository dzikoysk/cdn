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

import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class Modules implements CdnModule {

    private final List<CdnModule> modules = new ArrayList<>();

    public boolean isEmpty() {
        return modules.isEmpty();
    }

    public void addModule(CdnModule module) {
        modules.add(module);
    }

    @Override
    public String convertToCdn(String source) {
        return CdnUtils.process(modules, source, CdnModule::convertToCdn);
    }

    @Override
    public void visitDescription(StringBuilder output, String indentation, String description) {
        modules.forEach(module -> module.visitDescription(output, indentation, description));
    }

    @Override
    public void visitSectionOpening(StringBuilder output, String indentation, Section section) {
        modules.forEach(module -> module.visitSectionOpening(output, indentation, section));
    }

    @Override
    public void visitSectionEnding(StringBuilder output, String indentation, Section section) {
        modules.forEach(module -> module.visitSectionEnding(output, indentation, section));
    }

    @Override
    public Element<?> visitArrayValue(Element<?> element) {
        return CdnUtils.process(modules, element, CdnModule::visitArrayValue);
    }

    @Override
    public Element<?> resolveArrayValue(Element<?> element) {
        return CdnUtils.process(modules, element, CdnModule::resolveArrayValue);
    }

    @Override
    public boolean resolveArray(Stack<Section> sections, Piece value) {
        return CdnUtils.process(modules, false, (module, previousValue) -> previousValue || module.resolveArray(sections, value));
    }

}
