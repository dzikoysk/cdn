package net.dzikoysk.cdn.features;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnFeature;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;

import java.util.Arrays;
import java.util.Stack;

public class DefaultFeature implements CdnFeature {

    @Override
    public void visitSectionOpening(StringBuilder output, String indentation, Section section) {
        // Don't add space to unnamed sections
        // ~ https://github.com/dzikoysk/cdn/issues/29
        output.append(indentation)
                .append(section.getName())
                .append(section.getName().isEmpty() ? "" : " ").append(section.getOperators()[0])
                .append(CdnConstants.LINE_SEPARATOR);
    }

    @Override
    public void visitSectionEnding(StringBuilder output, String indentation, Section section) {
        output.append(indentation)
                .append(section.getOperators()[1])
                .append(CdnConstants.LINE_SEPARATOR);
    }

    @Override
    public boolean resolveArray(Stack<Section> sections, Unit value) {
        return !sections.isEmpty() && Arrays.equals(sections.peek().getOperators(), CdnConstants.ARRAY_SEPARATOR);
    }

}
