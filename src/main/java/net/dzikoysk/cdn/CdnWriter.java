package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnEntry;
import net.dzikoysk.cdn.model.CdnSection;
import org.panda_lang.utilities.commons.StringUtils;

final class CdnWriter {

    public String render(CdnElement<?> element) {
        StringBuilder content = new StringBuilder();
        render(content, 0, element);
        return content.toString().trim();
    }

    private void render(StringBuilder content, int level, CdnElement<?> element) {
        String indentation = StringUtils.buildSpace(level * 2);

        for (String comment : element.getComments()) {
            content.append(indentation)
                    .append(comment)
                    .append(CdnConstants.LINE_SEPARATOR);
        }

        if (element instanceof CdnEntry) {
            CdnEntry entry = (CdnEntry) element;

            content.append(indentation)
                    .append(entry.getName())
                    .append(CdnConstants.OPERATOR)
                    .append(" ")
                    .append(entry.getValue())
                    .append(CdnConstants.LINE_SEPARATOR);

            return;
        }

        if (element instanceof CdnSection) {
            CdnSection section = (CdnSection) element;
            content.append(indentation)
                    .append(section.getName())
                    .append(" {")
                    .append(CdnConstants.LINE_SEPARATOR);

            for (CdnElement<?> sectionElement : section.getValue().values()) {
                render(content, level + 1, sectionElement);
            }

            content.append(indentation)
                    .append("}")
                    .append(CdnConstants.LINE_SEPARATOR);
        }
    }

}
