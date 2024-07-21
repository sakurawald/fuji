package io.github.sakurawald.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static io.github.sakurawald.generator.JsonDocsGenerator.*;

@Slf4j
public class MarkdownDocsGenerator {

    @Getter
    private static final MarkdownDocsGenerator instance = new MarkdownDocsGenerator();

    private MarkdownDocsGenerator() {
    }

    private String toMarkdown(JsonObject root) {
        StringBuilder text = new StringBuilder();
        walk(text, 1, root);
        return text.toString();
    }

    private String translateDocumentation(String string, String indent) {
        StringBuilder text = new StringBuilder();

        // drop first and last quote
        if (string.charAt(0) == '\"') string = string.substring(1);
        if (string.charAt(string.length() - 1) == '\"') string = string.substring(0, string.length() - 1);

        // escape the java """ syntax
        string = string.translateEscapes();

        // add prefix indent
        String[] lines = string.split("\n");
        for (String line : lines) {
            text.append(indent).append(line).append(System.lineSeparator());

            // extra linefeed for markdown translate, so that what you get is what you see in ConfigModel.
            text.append(indent).append(System.lineSeparator());
        }

        // trim redundant linefeed.
        return text.toString().trim();
    }

    private String translateJsonPair(String key, JsonElement jsonElement, String indent) {
        StringBuilder text = new StringBuilder();
        text.append(indent).append("```json").append(System.lineSeparator())
                .append(indent).append("\"").append(key).append("\"").append(": ").append(jsonElement).append(System.lineSeparator())
                .append(indent).append("```").append(System.lineSeparator())
                .append(indent).append(System.lineSeparator());
        return text.toString();
    }

    private String getIndent(int level) {
        return ">".repeat(level) + " ";
    }

    private void walk(StringBuilder sb, int level, JsonObject node) {
        String indent = getIndent(level);

        Set<String> keys = node.keySet();
        for (String key : keys) {
            JsonElement value = node.get(key);

            /* process meta json-elements */
            if (key.endsWith(SKIP_WALK) || key.endsWith(FIELD_DOCUMENTATION)) continue;
            if (key.endsWith(CLASS_DOCUMENTATION)) {
                // class documentation
                sb.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                        .append(indent).append(System.lineSeparator())
                        .append(translateDocumentation(value.toString(), indent)).append(System.lineSeparator())
                        .append(indent).append("</td></tr></table>").append(System.lineSeparator());
                sb.append(indent).append(System.lineSeparator());
                continue;
            }

            /* process normal json-elements */
            if (value.isJsonObject()) {
                sb.append(getIndent(level + 1)).append("**%s**".formatted(key)).append(System.lineSeparator());

                boolean isModule = value.getAsJsonObject().has("enable");
                if (isModule) {
                    sb.append(getIndent(level + 1)).append(" <table><tr><td>module</td></tr></table>").append(System.lineSeparator());
                }

                sb.append(getIndent(level + 1)).append(System.lineSeparator());

                // note: skip walk
                if (keys.contains(key + SKIP_WALK)) {
                    sb.append(translateJsonPair(key, value, indent));
                    continue;
                }

                walk(sb, level + 1, (JsonObject) value);
            } else {
                if (keys.contains(key + FIELD_DOCUMENTATION)) {
                    // field documentation
                    String documentation = node.get(key + FIELD_DOCUMENTATION).getAsString();
                    sb.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                            .append(indent).append(System.lineSeparator())
                            .append(translateDocumentation(documentation, indent)).append(System.lineSeparator())
                            .append(indent).append(System.lineSeparator())
                            .append(indent).append(System.lineSeparator())
                            .append(translateJsonPair(key, value, indent))
                            .append(indent).append("</td></tr></table>").append(System.lineSeparator());
                } else {
                    // no documented field
                    sb.append(translateJsonPair(key, value, indent));
                }
            }

            // same level
            sb.append(indent).append(System.lineSeparator());
        }
    }

    public String generate(JsonObject jsonObject) {
        return toMarkdown(jsonObject);
    }
}
