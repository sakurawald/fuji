package io.github.sakurawald.module.initializer.skin.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import io.github.sakurawald.module.initializer.skin.enums.SkinVariant;
import io.github.sakurawald.util.IOUtil;

import java.io.IOException;
import java.net.URI;

public class MineSkinSkinProvider {

    private static final String API_SERVER = "https://api.mineskin.org/generate/url";
    private static final String USER_AGENT = "SkinRestorer";
    private static final String TYPE = "application/json";

    public static Property getSkin(String url, SkinVariant variant) {
        try {
            String param = ("{\"variant\":\"%s\",\"name\":\"%s\",\"visibility\":%d,\"url\":\"%s\"}")
                    .formatted(variant.toString(), "none", 1, url);
            JsonObject texture = JsonParser.parseString(IOUtil.post(URI.create(API_SERVER), param)).getAsJsonObject()
                    .getAsJsonObject("data").getAsJsonObject("texture");
            return new Property("textures", texture.get("value").getAsString(), texture.get("signature").getAsString());
        } catch (IOException e) {
            return null;
        }
    }
}
