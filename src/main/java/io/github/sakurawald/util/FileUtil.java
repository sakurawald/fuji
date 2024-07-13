package io.github.sakurawald.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class FileUtil {

    @SneakyThrows
    public static void compressFiles(List<File> input, File output) {
        if (!output.exists()) {
            output.createNewFile();
        }

        try (FileOutputStream fos = new FileOutputStream(output);
             ArchiveOutputStream<ZipArchiveEntry> archiveOut = new ZipArchiveOutputStream(fos)) {
            for (File file : input) {
                if (file.isFile() && file.exists()) {
                    ZipArchiveEntry entry = new ZipArchiveEntry(file, getEntryName(file));
                    archiveOut.putArchiveEntry(entry);
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            archiveOut.write(buffer, 0, len);
                        }
                    }
                    archiveOut.closeArchiveEntry();
                }
            }
        }
    }

    private static String getEntryName(File file) {
        return file.getParentFile().getName() + File.separator + file.getName();
    }

    public static List<Path> getLatestFiles(Path path) {
        try (Stream<Path> files = Files.list(path)) {
            return files
                    .filter(Files::isRegularFile)
                    .sorted((o1, o2) -> {
                        try {
                            FileTime t1 = Files.readAttributes(o1, BasicFileAttributes.class).creationTime();
                            FileTime t2 = Files.readAttributes(o2, BasicFileAttributes.class).creationTime();
                            return t1.compareTo(t2);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}