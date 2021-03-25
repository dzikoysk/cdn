package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.Map;

final class CdnWriter {

    private final Cdn cdn;

    CdnWriter(Cdn cdn) {
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

        // render multiline description
        for (String comment : element.getDescription()) {
            content.append(indentation)
                    .append(comment)
                    .append(CdnConstants.LINE_SEPARATOR);
        }

        // render simple entry
        if (element instanceof Entry) {
            Entry entry = (Entry) element;

            content.append(indentation)
                    .append(entry.getRecord())
                    .append(CdnConstants.LINE_SEPARATOR);

            return;
        }

        // render section
        if (element instanceof Section) {
            Section section = (Section) element;
            boolean isRoot = section instanceof Configuration;

            if (!isRoot) {
                content.append(indentation).append(section.getName());

                if (cdn.getSettings().isYamlLikeEnabled()) {
                    content.append(":");
                }
                else {
                    // Don't add space to unnamed sections
                    // ~ https://github.com/dzikoysk/cdn/issues/29
                    content.append(section.getName().isEmpty() ? "" : " ").append(section.getOperators()[0]);
                }

                content.append(CdnConstants.LINE_SEPARATOR);
            }

            // do not indent root sections
            int subLevel = isRoot
                    ? level
                    : level + 1;

            // render section content
            for (ConfigurationElement<?> sectionElement : section.getValue()) {
                render(content, subLevel, sectionElement);
            }

            // append opening operator for cdn format
            if (!isRoot && !cdn.getSettings().isYamlLikeEnabled()) {
                content.append(indentation)
                        .append(section.getOperators()[1])
                        .append(CdnConstants.LINE_SEPARATOR);
            }
        }
    }

}
