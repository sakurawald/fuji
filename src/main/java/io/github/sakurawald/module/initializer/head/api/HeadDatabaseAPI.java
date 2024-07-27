package io.github.sakurawald.module.initializer.head.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.LogUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("FieldCanBeLocal")

public class HeadDatabaseAPI {
    private final String API = "https://minecraft-heads.com/scripts/api.php?cat=%s&tags=true";
    private final Path STORAGE_PATH = Fuji.CONFIG_PATH.resolve("head").toAbsolutePath();

    public @NotNull Multimap<Category, Head> getHeads() {
        refreshCacheFromAPI();
        return loadCache();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void refreshCacheFromAPI() {

        for (Category category : Category.values()) {
            try {
                LogUtil.info("Saving {} heads to cache", category.name);
                URLConnection connection = URI.create(String.format(API, category.name)).toURL().openConnection();
                var stream = new BufferedInputStream(connection.getInputStream());
                FileUtils.copyInputStreamToFile(stream, STORAGE_PATH.resolve(category.name + ".json").toFile());
            } catch (IOException e) {
                LogUtil.warn("Failed to save new heads to cache");
            }

            if (!Files.exists(STORAGE_PATH.resolve(category.name + ".json"))) {
                LogUtil.info("Loading fallback {} heads", category.name);
                try {
                    Files.createDirectories(STORAGE_PATH);
                    Files.copy(
                            FabricLoader.getInstance().getModContainer(Fuji.MOD_ID).flatMap(modContainer -> modContainer.findPath("assets/fuji/cache/" + category.name + ".json")).get(),
                            STORAGE_PATH.resolve(category.name + ".json")
                    );
                } catch (IOException e) {
                    LogUtil.warn("Failed to load fallback heads", e);
                }
            }
        }
    }

    private @NotNull Multimap<Category, Head> loadCache() {
        Multimap<Category, Head> heads = HashMultimap.create();
        Gson gson = new Gson();
        for (Category category : Category.values()) {
            try {
                LogUtil.info("Loading {} heads from cache", category.name);
                var stream = Files.newInputStream(STORAGE_PATH.resolve(category.name + ".json"));
                JsonArray headsJson = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();
                for (JsonElement headJson : headsJson) {
                    try {
                        Head head = gson.fromJson(headJson, Head.class);
                        heads.put(category, head);
                    } catch (Exception e) {
                        LogUtil.warn("Invalid head: " + headJson);
                    }
                }
            } catch (IOException e) {
                LogUtil.warn("Failed to load heads from cache", e);
            }
        }
        LogUtil.info("Finished loading {} heads", heads.size());
        return heads;
    }
}
