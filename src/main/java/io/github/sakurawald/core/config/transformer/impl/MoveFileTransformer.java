package io.github.sakurawald.core.config.transformer.impl;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

@AllArgsConstructor
public class MoveFileTransformer extends ConfigurationTransformer {

    Path source;
    Path destination;

    @SneakyThrows
    @Override
    public void apply() {
        destination = destination.resolve(this.getPath().toFile().getName());

        if (Files.notExists(this.source) || Files.exists(destination)) return;

        Files.createDirectories(this.destination.getParent());
        logConsole("move to {}", destination);
        Files.move(source, destination);
    }

}
