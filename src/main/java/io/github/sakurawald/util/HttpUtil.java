package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

import static io.github.sakurawald.Fuji.log;

@UtilityClass
public class HttpUtil {
    public static String post(URI uri, String param) throws IOException {
        log.debug("post() -> uri = {}, param = {}", uri, param);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(uri);
            StringEntity params = new StringEntity(param);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    public static String get(URI uri) throws IOException {
        log.debug("get() -> uri = {}", uri);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(uri);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }
}
