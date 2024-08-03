package io.github.sakurawald.module.common.manager.impl.backup;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.IOUtil;
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

public class StandardBackupManager extends AbstractBackupManager {

    public StandardBackupManager() {
        super(Fuji.CONFIG_PATH.resolve("backup"));
    }

    protected boolean skipPath(@NotNull Path dir) {
        for (String other : Configs.configHandler.model().common.backup.skip) {
            if (dir.equals(Fuji.CONFIG_PATH.resolve(other))) return true;
        }

        return false;
    }

    @Override
    protected @NotNull List<File> getInputFiles() {
        List<File> files = new ArrayList<>();
        try {
            Files.walkFileTree(Fuji.CONFIG_PATH, new SimpleFileVisitor<>() {

                @Override
                public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir, BasicFileAttributes attrs) {
                    if (BACKUP_PATH.equals(dir)) return FileVisitResult.SKIP_SUBTREE;
                    if (skipPath(dir)) return FileVisitResult.SKIP_SUBTREE;

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

    protected void trimBackup() {
        List<Path> latestFiles = IOUtil.getLatestFiles(BACKUP_PATH);
        Iterator<Path> iterator = latestFiles.iterator();
        while (iterator.hasNext() && latestFiles.size() > Configs.configHandler.model().common.backup.max_slots - 1) {
            iterator.next().toFile().delete();
            iterator.remove();
        }
    }

    @Override
    protected @NotNull File getOutputFile() {
        String fileName = DateUtil.getCurrentDate() + ".zip";
        return BACKUP_PATH.resolve(fileName).toFile();
    }


    @Override
    protected void makeBackup() {
        IOUtil.compressFiles(getInputFiles(), getOutputFile());
    }

    @Override
    public void backup() {
        BACKUP_PATH.toFile().mkdirs();
        trimBackup();
        makeBackup();
    }
}
