package io.github.sakurawald.core.config.transformer.impl;

import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class MoveDirTransformer extends ConfigurationTransformer {

    final Path src;
    final Path dest;

    public MoveDirTransformer(Path src, Path dest) {
        this.src = src;
        this.dest = dest;
    }

    @SneakyThrows
    @Override
    public void apply() {
        if (Files.notExists(src ) || Files.exists(dest)) return;
        Files.move(src,dest);
    }
}
