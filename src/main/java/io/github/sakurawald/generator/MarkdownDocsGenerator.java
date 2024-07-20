package io.github.sakurawald.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Set;

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

            if (key.endsWith("@skip")) continue;
            if (value.isJsonObject()) {

                sb.append(getIndent(level + 1)).append("**%s**".formatted(key)).append(System.lineSeparator())
                        .append(getIndent(level + 1)).append(System.lineSeparator())
                ;

                // note: skip walk
                if (keys.contains(key + "@skip")) {
                    sb.append(translateJsonPair(key, value, indent));
                    continue;
                }

                walk(sb, level + 1, (JsonObject) value);
            } else {
                if (key.equals("class@documentation")) {
                    // class documentation
                    sb.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                            .append(translateDocumentation(value.toString(), indent)).append(System.lineSeparator())
                            .append(indent).append("</td></tr></table>").append(System.lineSeparator());

                } else if (keys.contains(key + "@documentation")) {
                    // field documentation
                    String documentation = node.get(key + "@documentation").getAsString();
                    sb.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                            .append(translateDocumentation(documentation, indent)).append(System.lineSeparator())
                            .append(indent).append(System.lineSeparator())
                            .append(indent).append(System.lineSeparator())
                            .append(translateJsonPair(key, value, indent))
                            .append(indent).append("</td></tr></table>").append(System.lineSeparator());
                } else {

                    // skip
                    if (key.endsWith("@documentation")) continue;

                    sb.append(translateJsonPair(key, value, indent));
                }
            }

            // same level
            sb.append(indent).append(System.lineSeparator());
        }
    }

    @SneakyThrows
    private void writeToFile(Path path, String string) {
        FileUtils.writeStringToFile(path.toFile(), string, Charset.defaultCharset());
    }

    public void generate(Path path, JsonObject jsonObject) {
        writeToFile(path, toMarkdown(jsonObject));
    }
}
