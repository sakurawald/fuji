package io.github.sakurawald.module.initializer.world_downloader;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.sakurawald.util.LogUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor

public class FileDownloadHandler implements HttpHandler {

    private static final long NANO_TO_S = 1000000000L;
    private final @NotNull WorldDownloaderInitializer module;
    private final @NotNull File file;
    private final int bytesPerSecond;

    @SuppressWarnings("BusyWait")
    @Override
    @SneakyThrows
    public void handle(@NotNull HttpExchange exchange) {
        LogUtil.info("Download file: {}", file.getAbsolutePath());

        /* consume this context */
        module.safelyRemoveContext(exchange.getHttpContext());

        /* transfer */
        if ("GET".equals(exchange.getRequestMethod())) {
            if (file.exists() && file.isFile()) {
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=" + file.getName());
                exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                long fileLength = file.length();
                exchange.sendResponseHeaders(200, fileLength);
                OutputStream os = exchange.getResponseBody();
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;

                long startTime = System.nanoTime();
                long bytesReadCount = 0;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    long currentTime = System.nanoTime();
                    long elapsedTime = currentTime - startTime;
                    long bytesReadExpected = (elapsedTime * bytesPerSecond) / NANO_TO_S;
                    if (bytesReadCount + bytesRead > bytesReadExpected) {
                        try {
                            long sleepTime = ((bytesReadCount + bytesRead - bytesReadExpected) * NANO_TO_S)
                                    / bytesPerSecond;
                            Thread.sleep(TimeUnit.NANOSECONDS.toMillis(sleepTime));
                        } catch (InterruptedException e) {
                            LogUtil.warn("Interrupted while sleeping for throttling", e);
                            return;
                        }
                    }

                    os.write(buffer, 0, bytesRead);
                    bytesReadCount += bytesRead;
                }
                fis.close();
                os.close();
            } else {
                String response = "File not found.";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        LogUtil.info("Delete file: {} -> {}", file.getAbsolutePath(), file.delete());
    }
}
