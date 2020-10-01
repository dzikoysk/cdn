package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

final class CdnReader {

    private final CDN cdn;
    private final Configuration root = new Configuration();
    private final Stack<Section> sections = new Stack<>();
    private List<String> description = new ArrayList<>();

    public CdnReader(CDN cdn) {
        this.cdn = cdn;
    }

    public Configuration read(String source) {
        if (cdn.getSettings().isIndentationEnabled()) {
            source = new CdnPrettier(source).tryToConvertIndentationInADumbWay();
        }

        String normalizedSource = StringUtils.replace(source.trim(), System.lineSeparator(), CdnConstants.LINE_SEPARATOR);

        List<String> lines = Arrays.stream(normalizedSource.split(CdnConstants.LINE_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());

        for (String line : lines) {
            line = line.trim();
            String originalLine = line;

            if (line.isEmpty() || line.startsWith(CdnConstants.COMMENT_OPERATORS[0]) || line.startsWith(CdnConstants.COMMENT_OPERATORS[1])) {
                description.add(line);
                continue;
            }

            if (line.endsWith(CdnConstants.SEPARATOR)) {
                line = line.substring(0, line.length() - CdnConstants.SEPARATOR.length()).trim();
            }

            boolean isArray = line.endsWith(CdnConstants.ARRAY_SEPARATOR[0]);

            // initialize section
            if ((isArray || line.endsWith(CdnConstants.OBJECT_SEPARATOR[0]))) {
                String sectionName = trimSeparator(line);

                if (sectionName.endsWith(CdnConstants.OPERATOR)) {
                    sectionName = sectionName.substring(0, sectionName.length() - CdnConstants.OPERATOR.length()).trim();
                }

                Section section = new Section(isArray ? CdnConstants.ARRAY_SEPARATOR : CdnConstants.OBJECT_SEPARATOR, sectionName, description);
                appendElement(section);
                sections.push(section); // has to be after append

                description = new ArrayList<>();
                continue;
            }
            // pop section
            else if (!sections.isEmpty() && line.endsWith(sections.peek().getOperators()[1])) {
                String lineBefore = trimSeparator(line);

                if (!lineBefore.isEmpty()) {
                    throw new UnsupportedOperationException("Unsupported section ending");
                }

                sections.pop();
                continue;
            }

            // add standard entry
            appendElement(Entry.of(originalLine, description));
            description = new ArrayList<>();
        }

        // map json-like formats with declared root operators
        if (root.size() == 1) {
            Section section = ObjectUtils.cast(Section.class, root.get(0));

            if (section != null && section.getName().isEmpty()) {
                return new Configuration(section.getValue());
            }
        }

        return root;
    }

    private void appendElement(ConfigurationElement<?> element) {
        if (sections.isEmpty()) {
            root.append(element);
        }
        else {
            sections.peek().append(element);
        }
    }

    private String trimSeparator(String line) {
        return line.substring(0, line.length() - 1).trim();
    }

}
