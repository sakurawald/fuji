package io.github.sakurawald.generator;

public class LexicographicalStringGenerator {
    private static final StringBuilder output = new StringBuilder();

    public static void main(String[] args) {
        generateCombinations(2);
        System.out.println(output.toString());
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