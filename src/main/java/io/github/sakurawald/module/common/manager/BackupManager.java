package io.github.sakurawald.module.common.manager;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.IOUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@UtilityClass
public class BackupManager {

    public static final Path BACKUP_PATH = Fuji.CONFIG_PATH.resolve("backup");

    private static boolean skipPath(Path dir)  {
        for (String other : Configs.configHandler.model().common.backup.skip) {
            if (dir.equals(Fuji.CONFIG_PATH.resolve(other))) return true;
        }

        return false;
    }

    private static List<File> getInputFiles() {
        List<File> files = new ArrayList<>();
        try {
            Files.walkFileTree(Fuji.CONFIG_PATH, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (BACKUP_PATH.equals(dir)) return FileVisitResult.SKIP_SUBTREE;
                    if (skipPath(dir)) return FileVisitResult.SKIP_SUBTREE;

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    files.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return files;
    }

    private static void trimBackup() {
        List<Path> latestFiles = IOUtil.getLatestFiles(BACKUP_PATH);
        Iterator<Path> iterator = latestFiles.iterator();
        while (iterator.hasNext() && latestFiles.size() > Configs.configHandler.model().common.backup.max_slots - 1) {
            iterator.next().toFile().delete();
            iterator.remove();
        }
    }

    private static File getOutputFile() {
        String fileName = DateUtil.getCurrentDate() + ".zip";
        return BACKUP_PATH.resolve(fileName).toFile();
    }

    private static void newBackup() {
        IOUtil.compressFiles(getInputFiles(), getOutputFile());
    }

    public static void backup() {
        BACKUP_PATH.toFile().mkdirs();
        trimBackup();
        newBackup();
    }
}
