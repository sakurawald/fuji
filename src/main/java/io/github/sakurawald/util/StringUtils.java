package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;

@UtilityClass
public class StringUtils {

    public static String readString(BufferedReader reader) throws IOException {
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        return response.toString();
    }

}
