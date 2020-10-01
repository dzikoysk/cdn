package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.Map;

final class CdnWriter {

    private final CDN cdn;

    CdnWriter(CDN cdn) {
        this.cdn = cdn;
    }

    public String render(ConfigurationElement<?> element) {
        StringBuilder content = new StringBuilder();
        render(content, 0, element);
        String result = content.toString();

        for (Map.Entry<? extends String, ? extends String> entry : cdn.getSettings().getPlaceholders().entrySet()) {
            result = StringUtils.replace(result, "${{" + entry.getKey() + "}}", entry.getValue());
        }

        return result.trim();
    }

    private void render(StringBuilder content, int level, ConfigurationElement<?> element) {
        String indentation = StringUtils.buildSpace(level * 2);

        for (String comment : element.getDescription()) {
            content.append(indentation)
                    .append(comment)
                    .append(CdnConstants.LINE_SEPARATOR);
        }

        if (element instanceof Entry) {
            Entry entry = (Entry) element;

            content.append(indentation)
                    .append(entry.getRecord())
                    .append(CdnConstants.LINE_SEPARATOR);

            return;
        }

        if (element instanceof Section) {
            Section section = (Section) element;
            boolean isRoot = section instanceof Configuration;

            if (!isRoot) {
                content.append(indentation).append(section.getName());

                if (cdn.getSettings().isIndentationEnabled()) {
                    content.append(":");
                }
                else {
                    content.append(" ").append(section.getOperators()[0]);
                }

                content.append(CdnConstants.LINE_SEPARATOR);
            }

            int subLevel = isRoot ? level : level + 1;

            for (ConfigurationElement<?> sectionElement : section.getValue()) {
                render(content, subLevel, sectionElement);
            }

            if (!isRoot && !cdn.getSettings().isIndentationEnabled()) {
                content.append(indentation)
                        .append(section.getOperators()[1])
                        .append(CdnConstants.LINE_SEPARATOR);
            }
        }
    }

}
