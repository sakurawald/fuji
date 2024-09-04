package io.github.sakurawald.module.initializer.skin.config;

import com.mojang.authlib.properties.Property;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.auxiliary.LogUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

public class SkinIO {

    private static final String FILE_EXTENSION = ".json";

    private final Path savePath;

    public SkinIO(Path savePath) {
        this.savePath = savePath;
    }

    public @Nullable Property loadSkin(UUID uuid) {
        File file = savePath.resolve(uuid + FILE_EXTENSION).toFile();
        try {
            String string = org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return ConfigHandler.getGson().fromJson(string, Property.class);
        } catch (IOException e) {
            LogUtil.error("Load skin failed: " + e.getMessage());
        }
        return null;
    }

    public void saveSkin(UUID uuid, Property skin) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(new File(savePath.toFile(), uuid + FILE_EXTENSION), ConfigHandler.getGson().toJson(skin), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.error("Save skin failed: " + e.getMessage());
        }
    }
}
