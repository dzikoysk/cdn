package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;

import java.util.Stack;

public interface CdnFeature {

    default String convertToCdn(String source) { return source; }

    default void visitSectionOpening(StringBuilder output, String indentation, Section section) { }

    default void visitSectionEnding(StringBuilder output, String indentation, Section section) { }

    default Element<?> visitArrayValue(Element<?> element) { return element; }

    default Element<?> resolveArrayValue(Element<?> element) { return element; }

    default boolean resolveArray(Stack<Section> sections, Unit value) { return false; }

}
