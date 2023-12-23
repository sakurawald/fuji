package io.github.sakurawald.module.initializer.skin.io;

import com.mojang.authlib.properties.Property;
import io.github.sakurawald.config.handler.ConfigHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

import static io.github.sakurawald.Fuji.LOGGER;

public class SkinIO {

    private static final String FILE_EXTENSION = ".json";

    private final Path savePath;

    public SkinIO(Path savePath) {
        this.savePath = savePath;
    }

    public Property loadSkin(UUID uuid) {
        File file = savePath.resolve(uuid + FILE_EXTENSION).toFile();
        try {
            String string = org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return ConfigHandler.getGson().fromJson(string, Property.class);
        } catch (IOException e) {
            LOGGER.error("Load skin failed: " + e.getMessage());
        }
        return null;
    }

    public void saveSkin(UUID uuid, Property skin) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(new File(savePath.toFile(), uuid + FILE_EXTENSION), ConfigHandler.getGson().toJson(skin), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Save skin failed: " + e.getMessage());
        }
    }
}
