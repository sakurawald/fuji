package io.github.sakurawald.core.config.transformer.impl;

import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor
public class MoveFileTransformer extends ConfigurationTransformer {

    final Path destination;

    @SneakyThrows
    @Override
    public void apply() {
        if (Files.notExists(this.getPath()) || Files.exists(destination)) return;

        Files.createDirectories(this.destination);
        logConsole("move to {}", destination);
        Files.move(this.getPath(), destination.resolve(this.getPath().toFile().getName()));
    }

}
