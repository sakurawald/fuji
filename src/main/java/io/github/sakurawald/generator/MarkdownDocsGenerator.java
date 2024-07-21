package io.github.sakurawald.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static io.github.sakurawald.generator.JsonDocsGenerator.*;

@SuppressWarnings("StringBufferReplaceableByString")
@Slf4j
public class MarkdownDocsGenerator {

    @Getter
    private static final MarkdownDocsGenerator instance = new MarkdownDocsGenerator();

    private MarkdownDocsGenerator() {}

    private String toMarkdown(JsonObject root) {
        StringBuilder text = new StringBuilder();
        walk(text, 1, root);
        return text.toString();
    }

    private String getIndent(int level) {
        return ">".repeat(level) + " ";
    }

    private String makeDocumentation(String string, int level) {
        StringBuilder text = new StringBuilder();

        // drop first and last quote
        if (string.charAt(0) == '\"') string = string.substring(1);
        if (string.charAt(string.length() - 1) == '\"') string = string.substring(0, string.length() - 1);

        // escape the java """ syntax
        string = string.translateEscapes();

        // add prefix indent
        String[] lines = string.split("\n");
        String indent = getIndent(level);
        for (String line : lines) {
            text.append(indent).append(line).append(System.lineSeparator());

            // extra linefeed for markdown translate, so that what you get is what you see in ConfigModel.
            text.append(indent).append(System.lineSeparator());
        }

        // trim redundant linefeed.
        return text.toString().trim();
    }

    private String makeJsonPair(String key, JsonElement jsonElement, int level) {
        StringBuilder text = new StringBuilder();
        String indent = getIndent(level);
        text.append(indent).append("```json").append(System.lineSeparator())
                .append(indent).append("\"").append(key).append("\"").append(": ").append(jsonElement).append(System.lineSeparator())
                .append(indent).append("```").append(System.lineSeparator())
                .append(indent).append(System.lineSeparator());
        return text.toString();
    }

    private String makeBoxed(String string, int level) {
        StringBuilder text = new StringBuilder();
        String indent = getIndent(level);
        // class documentation
        text.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                .append(indent).append(System.lineSeparator())
                .append(string)
                .append(indent).append("</td></tr></table>").append(System.lineSeparator())
                .append(indent).append(System.lineSeparator());

        return text.toString();
    }

    private String makeDocumentedJsonPair(JsonObject node, String key, int level) {
        String documentation = node.get(key + FIELD_DOCUMENTATION).getAsString();
        String indent = getIndent(level);

        StringBuilder text = new StringBuilder();
        text.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                .append(indent).append(System.lineSeparator())
                // require 2 lines to let the parser work
                .append(makeDocumentation(documentation, level)).append(System.lineSeparator())
                .append(indent).append(System.lineSeparator())
                .append(indent).append(System.lineSeparator())
                //
                .append(makeJsonPair(key, node.get(key), level))

                .append(indent).append("</td></tr></table>").append(System.lineSeparator());


        return text.toString();
    }

    private void processJsonPair(StringBuilder text, JsonObject node, String key, int level) {
        if (node.keySet().contains(key + FIELD_DOCUMENTATION)) {
            // field documentation
            text.append(makeDocumentedJsonPair(node, key, level));
        } else {
            // no documented field
            text.append(makeJsonPair(key, node.get(key), level));
        }
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
                sb.append(makeBoxed(makeDocumentation(value.toString(), level) + System.lineSeparator(), level));
                continue;
            }

            /* process normal json-elements */
            if (value.isJsonObject()) {
                sb.append(getIndent(level + 1)).append("**%s**".formatted(key)).append(System.lineSeparator());

                boolean isModule = value.getAsJsonObject().has("enable");
                if (isModule) {
                    sb.append(getIndent(level + 1)).append(" `module`").append(System.lineSeparator());
                }

                sb.append(getIndent(level + 1)).append(System.lineSeparator());

                // note: skip walk
                if (keys.contains(key + SKIP_WALK)) {
                    processJsonPair(sb, node, key, level);
                    continue;
                }

                walk(sb, level + 1, (JsonObject) value);
            } else {
                processJsonPair(sb, node, key, level);
            }

            // same level
            sb.append(indent).append(System.lineSeparator());
        }
    }

    public String generate(JsonObject jsonObject) {
        return toMarkdown(jsonObject);
    }
}
