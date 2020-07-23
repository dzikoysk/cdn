package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnEntry;
import net.dzikoysk.cdn.model.CdnRoot;
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

        for (String comment : element.getDescription()) {
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
            boolean isRoot = section instanceof CdnRoot;

            if (!isRoot) {
                content.append(indentation)
                        .append(section.getName())
                        .append(" {")
                        .append(CdnConstants.LINE_SEPARATOR);
            }

            int subLevel = isRoot ? level : level + 1;

            for (CdnElement<?> sectionElement : section.getValue().values()) {
                render(content, subLevel, sectionElement);
            }

            if (!isRoot) {
                content.append(indentation)
                        .append("}")
                        .append(CdnConstants.LINE_SEPARATOR);
            }
        }
    }

}
