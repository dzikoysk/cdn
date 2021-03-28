package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.Map;

final class CdnWriter {

    private final CdnSettings settings;

    CdnWriter(CdnSettings settings) {
        this.settings = settings;
    }

    public String render(Element<?> element) {
        StringBuilder content = new StringBuilder();
        render(content, 0, element);
        String result = content.toString();

        for (Map.Entry<? extends String, ? extends String> entry : settings.getPlaceholders().entrySet()) {
            result = StringUtils.replace(result, "${{" + entry.getKey() + "}}", entry.getValue());
        }

        return result.trim();
    }

    private void render(StringBuilder output, int level, Element<?> element) {
        String indentation = StringUtils.buildSpace(level * 2);

        // render multiline description
        for (String comment : element.getDescription()) {
            output.append(indentation)
                    .append(comment)
                    .append(CdnConstants.LINE_SEPARATOR);
        }

        // render simple entry
        if (element instanceof Entry) {
            Entry entry = (Entry) element;

            output.append(indentation)
                    .append(entry.getRecord())
                    .append(CdnConstants.LINE_SEPARATOR);

            return;
        }

        // render section
        if (element instanceof Section) {
            Section section = (Section) element;
            boolean isRoot = section instanceof Configuration;

            if (!isRoot) {
                for (CdnFeature feature : settings.getFeatures()) {
                    feature.visitSectionOpening(output, indentation, section);
                }
            }

            // do not indent root sections
            int subLevel = isRoot
                    ? level
                    : level + 1;

            // render section content
            for (Element<?> sectionElement : section.getValue()) {
                render(output, subLevel, sectionElement);
            }

            // append opening operator for cdn format
            if (!isRoot) {
                for (CdnFeature feature : settings.getFeatures()) {
                    feature.visitSectionEnding(output, indentation, section);
                }
            }

            return;
        }

        if (element instanceof Unit) {
            output.append(indentation)
                    .append(((Unit) element).getValue())
                    .append(CdnConstants.LINE_SEPARATOR);

            return;
        }

        throw new IllegalStateException("Unknown element: " + element);
    }

}
