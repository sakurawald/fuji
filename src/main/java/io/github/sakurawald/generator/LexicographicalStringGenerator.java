package io.github.sakurawald.generator;

import com.google.gson.annotations.SerializedName;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import oshi.util.FileUtil;

import java.nio.charset.Charset;
import java.nio.file.Path;

public class LexicographicalStringGenerator {
    private static final StringBuilder output = new StringBuilder();

    @SneakyThrows
    public static void main(String[] args) {
        generateCombinations( 2);
        FileUtils.writeStringToFile(Path.of("alpha-table.txt").toFile(), String.valueOf(output), Charset.defaultCharset());
    }

    public static void generateCombinations(int length) {
        char[] chars = new char[length];
        generateCombinationsHelper(chars, 0, length);
    }

    private static void generateCombinationsHelper(char[] chars, int index, int length) {
        if (index == length) {
            String str = new String(chars);
            output.append("\"").append(str).append("\"").append(",");
            return;
        }

        for (char ch = 'a'; ch <= 'z'; ch++) {
            chars[index] = ch;
            generateCombinationsHelper(chars, index + 1, length);
        }
    }
}