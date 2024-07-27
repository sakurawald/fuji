package io.github.sakurawald.module.common.structure;

import io.github.sakurawald.util.LogUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Downloader {

    URL url;
    File destination;

    public Downloader(URL url, File destination) {
        this.url = url;
        this.destination = destination;
    }

    public void start() {
        CompletableFuture.runAsync(() -> {
            try {
                FileUtils.copyURLToFile(url, destination);
                onComplete();
                LogUtil.info("[Downloader] Save file from {} to {} done.", url, destination);
            } catch (IOException e) {
                LogUtil.error("[Downloader] Failed to download the file from {} to {}", url, destination);
            }
        });
    }

    public void onComplete(){
        // no-op
    }


}
