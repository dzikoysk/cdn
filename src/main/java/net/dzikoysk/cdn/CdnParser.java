package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnSection;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

final class CdnParser {

    private static final String LINE_SEPARATOR = "\n";
    private static final String[] COMMENT_OPERATOR = { "#", "//" };
    private static final String[] OBJECT_SEPARATOR = { "{", "}" };
    private static final String[] ARRAY_SEPARATOR = { "[", "]" };

    public CdnSection parse(String source) {
        String normalizedSource = StringUtils.replace(source.trim(), System.lineSeparator(), LINE_SEPARATOR);

        List<String> lines = Arrays.stream(normalizedSource.split(LINE_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());

        Map<String, CdnElement<?>> root = new HashMap<>();
        Stack<Map<String, CdnElement<?>>> sections = new Stack<>();
        Stack<String> operators = new Stack<>();
        List<String> comments = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith(COMMENT_OPERATOR[0]) || line.startsWith(COMMENT_OPERATOR[1])) {
                comments.add(line);
                continue;
            }

            if (line.endsWith(OBJECT_SEPARATOR[0])) {
                String sectionName = trimSeparator(line);
                operators.push(OBJECT_SEPARATOR[0]);

                Map<String, CdnElement<?>> content = new HashMap<>();
                sections.push(content);

                CdnSection section = new CdnSection(sectionName, content, comments);
                root.put(sectionName, section);

                comments = new ArrayList<>();
            }
            else if (line.endsWith(OBJECT_SEPARATOR[1]) && operators.peek().equals(OBJECT_SEPARATOR[0])) {
                String lineBefore = trimSeparator(line);
                operators.pop();
                sections.pop();
            }

            // sections.peek().put()
        }

        return new CdnSection("", root, Collections.emptyList());
    }

    private String trimSeparator(String line) {
        return line.substring(0, line.length() - 1).trim();
    }

}
