package io.github.sakurawald.core.manager.impl.attachment;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.manager.abst.BaseManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AttachmentManager extends BaseManager {

    private static final Path ATTACHMENT_STORAGE_PATH = Fuji.CONFIG_PATH.resolve("attachment");

    @Override
    public void onInitialize() {

    }


    @SuppressWarnings("DataFlowIssue")
    public List<String> listSubjectName() {
        try {
            return Arrays.stream(ATTACHMENT_STORAGE_PATH.toFile().listFiles()).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public List<String> listSubjectId(String subject) {
        try {
            return Arrays.stream(ATTACHMENT_STORAGE_PATH.resolve(subject).toFile().listFiles()).filter(File::isFile).map(File::getName).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getAttachmentFile(String subject, String uuid) {
        File file = ATTACHMENT_STORAGE_PATH.resolve(subject).resolve(uuid).toFile();
        file.getParentFile().mkdirs();
        return file;
    }

    public void setAttachment(String subject, String uuid, String data) throws IOException {
        File file = this.getAttachmentFile(subject, uuid);
        FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
    }

    public String getAttachment(String subject, String uuid) throws IOException {
        File file = this.getAttachmentFile(subject, uuid);
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    public boolean unsetAttachment(String subject, String uuid) {
        File file = this.getAttachmentFile(subject, uuid);
        return file.delete();
    }

}
