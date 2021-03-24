package net.dzikoysk.cdn;

import net.dzikoysk.cdn.converters.YamlConverter;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

final class CdnReader {

    private final CdnSettings settings;
    private final Configuration root = new Configuration();
    private final Stack<Section> sections = new Stack<>();
    private List<String> description = new ArrayList<>();

    CdnReader(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration read(String source) {
        if (settings.isYamlLikeEnabled()) {
            source = new YamlConverter().convertToCdn(source);
        }

        // replace system-dependent line separators with unified one
        String normalizedSource = StringUtils.replace(source.trim(), System.lineSeparator(), CdnConstants.LINE_SEPARATOR);

        List<String> lines = Arrays.stream(normalizedSource.split(CdnConstants.LINE_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());

        for (String line : lines) {
            line = line.trim();
            String originalLine = line;

            // handle description
            if (line.isEmpty() || line.startsWith(CdnConstants.COMMENT_OPERATORS[0]) || line.startsWith(CdnConstants.COMMENT_OPERATORS[1])) {
                description.add(line);
                continue;
            }

            // remove operator at the end of line
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

                // skip values with section operators
                if (lineBefore.isEmpty()) {
                    sections.pop();
                    continue;
                }
            }

            // add standard entry
            appendElement(Entry.of(originalLine, description));
            description = new ArrayList<>();
        }

        // flat map json-like formats with declared root operators
        return Option.when(root.size() == 1, root)
                .flatMap(root -> root.getSection(0))
                .filter(element -> element.getName().isEmpty())
                .map(element -> new Configuration(element.getValue()))
                .orElseGet(root);
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
