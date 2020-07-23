package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnEntry;
import net.dzikoysk.cdn.model.CdnRoot;
import net.dzikoysk.cdn.model.CdnSection;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

final class CdnReader {

    private final Cdn cdn;
    private final CdnRoot root = new CdnRoot();
    private final Stack<CdnSection> sections = new Stack<>();
    private final Stack<String> operators = new Stack<>();
    private List<String> comments = new ArrayList<>();

    public CdnReader(Cdn cdn) {
        this.cdn = cdn;
    }

    public CdnRoot read(String source) {
        String normalizedSource = StringUtils.replace(source.trim(), System.lineSeparator(), CdnConstants.LINE_SEPARATOR);

        List<String> lines = Arrays.stream(normalizedSource.split(CdnConstants.LINE_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith(CdnConstants.COMMENT_OPERATORS[0]) || line.startsWith(CdnConstants.COMMENT_OPERATORS[1])) {
                comments.add(line);
                continue;
            }

            // initialize section
            if (line.endsWith(CdnConstants.OBJECT_SEPARATOR[0])) {
                String sectionName = trimSeparator(line);
                operators.push(CdnConstants.OBJECT_SEPARATOR[0]);

                CdnSection section = new CdnSection(sectionName, comments);
                appendElement(section);
                sections.push(section); // has to be after append

                comments = new ArrayList<>();
                continue;
            }
            // pop section
            else if (line.endsWith(CdnConstants.OBJECT_SEPARATOR[1]) && operators.peek().equals(CdnConstants.OBJECT_SEPARATOR[0])) {
                String lineBefore = trimSeparator(line);

                if (!lineBefore.isEmpty()) {
                    throw new UnsupportedOperationException("Unsupported section ending");
                }

                operators.pop();
                sections.pop();
                continue;
            }

            // add standard entry
            String[] elements = StringUtils.split(line, CdnConstants.OPERATOR);
            CdnEntry entry = new CdnEntry(elements[0].trim(), comments, elements[1].trim());
            appendElement(entry);
            comments = new ArrayList<>();
        }

        return root;
    }

    private void appendElement(CdnElement<?> element) {
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
