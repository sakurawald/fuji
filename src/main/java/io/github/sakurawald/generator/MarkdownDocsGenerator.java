package io.github.sakurawald.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.jna.platform.unix.X11;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

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
        StringBuilder sb = new StringBuilder();
        walk(sb, 1, root);
        return sb.toString();
    }

    public String translateToMarkdown(String string, String indent) {
        StringBuilder ret = new StringBuilder();
        String[] lines = string.split("\n");

        for (String line : lines) {
            line = line.replace("\"", "");
            ret.append(indent).append(line).append(System.lineSeparator());
        }

        return ret.toString().trim();
    }

    private String toJsonPair(String key, JsonElement element){
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(key).append("\"").append(": ");

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                sb.append(element);
            }
            if (primitive.isNumber()) {
                sb.append(element);
            }
            if (primitive.isBoolean()) {
                sb.append(element);
            }
        } else {
            sb.append(element);
        }

        return sb.toString();
    }

    private String getIndent(int level) {
        return StringUtils.repeat(">", level) + " ";
    }

    private void walk(StringBuilder sb, int level, JsonObject node) {
        String indent = getIndent(level);

        Set<String> keys = node.keySet();
        for (String key : keys) {
            JsonElement jsonElement = node.get(key);

            JsonElement value = node.get(key);
            if (key.endsWith("@skip")) continue;
            if (key.endsWith("@documentation")) {
//                sb.append(indent).append("<table><tr><td>")
//                        .append(translateToMarkdown(value.toString(), indent)).append(System.lineSeparator())
//                        .append(indent).append("</td></tr></table>").append(System.lineSeparator())
//                        .append(indent).append(System.lineSeparator());
            } else {

                if (jsonElement.isJsonObject()) {
                    sb.append(getIndent(level + 1)).append("**%s**".formatted(key)).append(System.lineSeparator())
                            .append(getIndent(level + 1)).append(System.lineSeparator())
                            .append(getIndent(level + 1)).append(System.lineSeparator());;

                    // note: skip walk
                    if (keys.contains(key + "@skip")) {
                        log.warn("skip key {}", key);
                        sb.append(indent).append("```json").append(System.lineSeparator())
                                .append(indent).append(toJsonPair(key, value)).append(System.lineSeparator())
                                .append(indent).append("```").append(System.lineSeparator())
                                .append(indent).append(System.lineSeparator());
                        continue;
                    }

                    walk(sb, level + 1, (JsonObject) jsonElement);
                } else {

                    StringBuilder jsonPair = new StringBuilder().append(indent).append("```json").append(System.lineSeparator())
                            .append(indent).append(toJsonPair(key, value)).append(System.lineSeparator())
                            .append(indent).append("```").append(System.lineSeparator())
                            .append(indent).append(System.lineSeparator());

                    if (keys.contains(key + "@documentation")) {
                        String documentation = node.get(key + "@documentation").getAsString();
                        sb.append(indent).append("<table><tr><td>").append(System.lineSeparator())
                                .append(translateToMarkdown(documentation, indent)).append(System.lineSeparator())
                                .append(indent).append(System.lineSeparator())
                                .append(jsonPair)
                                .append(indent).append("</td></tr></table>").append(System.lineSeparator());
                    } else {
                        sb.append(jsonPair);
                    }


                }
            }

            // same-level
            sb.append(indent).append(System.lineSeparator());

        }

    }


    @SneakyThrows
    private void writeToFile(Path path, String string) {
        path.toFile().getParentFile().mkdirs();
        FileUtils.writeStringToFile(path.toFile(), string, Charset.defaultCharset());
    }

    public void generate(Path path, JsonObject jsonObject) {
        writeToFile(path, toMarkdown(jsonObject));
    }
}
