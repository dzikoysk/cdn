package net.dzikoysk.cdn.features;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnFeature;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;

import java.util.Stack;

import static net.dzikoysk.cdn.CdnConstants.ARRAY;

public final class YamlFeature implements CdnFeature {

    @Override
    public String convertToCdn(String source) {
        YamlConverter converter = new YamlConverter(source);
        return converter.convert();
    }

    @Override
    public void visitSectionOpening(StringBuilder output, String indentation, Section section) {
        output.append(":");
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
