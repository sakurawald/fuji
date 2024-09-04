package io.github.sakurawald.module.common.structure;

import io.github.sakurawald.core.auxiliary.LogUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
                LogUtil.info("[Downloader] Start download file from {} to {}.", url, destination);
                FileUtils.copyURLToFile(url, destination);
                onComplete();
                LogUtil.info("[Downloader] End download file from {} to {}.", url, destination);
            } catch (IOException e) {
                LogUtil.error("[Downloader] Failed to download file from {} to {}", url, destination);
            }
        });
    }

    public void onComplete(){
        // no-op
    }


}
