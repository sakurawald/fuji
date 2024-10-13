package io.github.sakurawald.module.initializer.skin.structure;

import com.mojang.authlib.properties.Property;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class SkinIO {

    private final Path rootPath;

    public SkinIO(Path rootPath) {
        this.rootPath = rootPath;
    }

    private Path computeFilePath(UUID uuid) {
        return rootPath.resolve(uuid + ".json");
    }

    public @Nullable Property loadSkin(UUID uuid) {
        Path playerData = this.computeFilePath(uuid);
        if (Files.notExists(playerData)) return null;

        try {
            String string = Files.readString(playerData);
            return BaseConfigurationHandler.getGson().fromJson(string, Property.class);
        } catch (IOException e) {
            LogUtil.error("load skin failed: " + e.getMessage());
        }
        return null;
    }

    public void saveSkin(UUID uuid, Property skin) {
        try {
            File file = computeFilePath(uuid).toFile();
            FileUtils.writeStringToFile(file, BaseConfigurationHandler.getGson().toJson(skin), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.error("save skin failed: " + e.getMessage());
        }
    }
}
