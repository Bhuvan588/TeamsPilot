package com.example.teampilot.document.service;

import com.example.teampilot.document.model.DocumentSection;
import com.example.teampilot.document.model.DocumentStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DocumentStructureAnalyzer {

    private static final Pattern MARKDOWN_HEADING =
            Pattern.compile("^#{1,6}\\s+.+$");

    private static final Pattern NUMBERED_HEADING =
            Pattern.compile("^\\d+(?:\\.\\d+)*[.)]?\\s+\\S.{0,100}$");

    private static final Pattern UPPERCASE_HEADING =
            Pattern.compile("[A-Z][A-Z0-9 &/()_-]{2,60}");

    public DocumentStructure analyze(String text) {

        String[] lines = text.split("\\R");

        List<Integer> headingLines = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();

            if (isHeading(line)) {
                headingLines.add(i);
            }
        }

        List<DocumentSection> sections = new ArrayList<>();

        for (int i = 0; i < headingLines.size(); i++) {

            int start = headingLines.get(i);
            int end = i + 1 < headingLines.size()
                    ? headingLines.get(i + 1) - 1
                    : lines.length - 1;

            String heading = lines[start].trim()
                    .replaceFirst("^#{1,6}\\s+", "");

            StringBuilder content = new StringBuilder();

            for (int j = start + 1; j <= end; j++) {
                content.append(lines[j]).append("\n");
            }

            sections.add(new DocumentSection(
                    heading,
                    content.toString().trim(),
                    start,
                    end
            ));
        }

        // Two or more detected headings are required
        // for our initial structured-document classification.
        boolean structured = sections.size() >= 2;

        double confidence = Math.min(
                1.0,
                sections.size() / 4.0
        );

        return new DocumentStructure(
                sections,
                confidence,
                structured
        );
    }

    private boolean isHeading(String line) {

        if (line.isBlank()) {
            return false;
        }

        if (MARKDOWN_HEADING.matcher(line).matches()) {
            return true;
        }

        if (NUMBERED_HEADING.matcher(line).matches()) {
            return true;
        }

        return line.equals(line.toUpperCase())
                && UPPERCASE_HEADING.matcher(line).matches()
                && line.split("\\s+").length <= 8;
    }
}