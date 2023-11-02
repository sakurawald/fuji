package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static io.github.sakurawald.Fuji.LOGGER;

@UtilityClass
public class HttpUtil {
    public static String post(URI uri, String param) throws IOException {
        LOGGER.debug("post() -> uri = {}, param = {}", uri, param);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Fuji");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        IOUtils.write(param.getBytes(StandardCharsets.UTF_8), connection.getOutputStream());
        return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
    }

    public static String get(URI uri) throws IOException {
        LOGGER.debug("get() -> uri = {}", uri);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
    }
}
