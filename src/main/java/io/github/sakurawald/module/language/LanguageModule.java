package io.github.sakurawald.module.language;

import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class LanguageModule extends AbstractModule {

    private final Path STORAGE_PATH = ServerMain.CONFIG_PATH.resolve("language").toAbsolutePath();

    @Override
    public void onInitialize() {
        loadLanguages();
    }

    public void loadLanguages() {
        if (!Files.exists(STORAGE_PATH)) {
            log.info("Create language folder.");
            try {
                Files.createDirectories(STORAGE_PATH);
                FileUtils.copyDirectory(FabricLoader.getInstance().getModContainer(ServerMain.MOD_ID).flatMap(modContainer -> modContainer.findPath("assets/sakurawald/lang")).get().toFile(), STORAGE_PATH.toFile());
            } catch (IOException e) {
                log.warn("Failed to create language folder -> {}", e.getMessage());
            }
        }
    }
}
