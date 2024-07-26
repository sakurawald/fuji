package io.github.sakurawald.module.common.manager;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.IOUtil;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@UtilityClass
public class BackupRescueManager {

    public static final Path BACKUP_PATH = Fuji.CONFIG_PATH.resolve("backup_rescue");

    private static @NotNull List<File> getInputFiles() {
        List<File> files = new ArrayList<>();
        try {
            Files.walkFileTree(Fuji.CONFIG_PATH, new SimpleFileVisitor<>() {

                @Override
                public @NotNull FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (BACKUP_PATH.equals(dir)) return FileVisitResult.SKIP_SUBTREE;

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, BasicFileAttributes attrs) {
                    files.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return files;
    }

    private static @NotNull File getOutputFile() {
        String fileName = DateUtil.getCurrentDate() + ".zip";
        return BACKUP_PATH.resolve(fileName).toFile();
    }

    private static void newBackup() {
        IOUtil.compressFiles(getInputFiles(), getOutputFile());
    }

    public static void backup() {
        BACKUP_PATH.toFile().mkdirs();
        newBackup();
    }
}
