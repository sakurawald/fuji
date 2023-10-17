package io.github.sakurawald.module.world_downloader;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.mixin.resource_world.MinecraftServerAccessor;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class WorldDownloaderModule extends AbstractModule {

    private EvictingQueue<String> contextQueue;
    private HttpServer server;


    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.world_downloader.enable;
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        contextQueue = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.world_downloader.context_cache_size);
    }

    @Override
    public void onReload() {
        this.initServer();
    }

    public void initServer() {
        if (server != null) {
            server.stop(0);
        }

        try {
            server = HttpServer.create(new InetSocketAddress(ConfigManager.configWrapper.instance().modules.world_downloader.port), 0);
            server.start();
        } catch (IOException e) {
            log.error("Failed to start http server: " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("download").executes(this::$download));
    }

    public void safelyRemoveContext(String path) {
        try {
            this.server.removeContext(path);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    public void safelyRemoveContext(HttpContext httpContext) {
        safelyRemoveContext(httpContext.getPath());
    }

    @SuppressWarnings("SameReturnValue")
    @SneakyThrows
    private int $download(CommandContext<CommandSourceStack> ctx) {

        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        /* init server */
        if (server == null) {
            initServer();
        }

        /* remove redundant contexts */
        if (contextQueue.remainingCapacity() == 0) {
            log.info("contexts is full, remove the oldest context. {}", contextQueue.peek());
            safelyRemoveContext(contextQueue.poll());
        }

        /* create context */
        String path = "/download/" + UUID.randomUUID();
        contextQueue.add(path);
        File file = compressRegionFile(player);
        double BYTE_TO_MEGABYTE = 1.0 * 1024 * 1024;
        MessageUtil.sendBroadcast("world_downloader.request", player.getGameProfile().getName(), file.length() / BYTE_TO_MEGABYTE);
        server.createContext(path, new FileDownloadHandler(this, file, ConfigManager.configWrapper.instance().modules.world_downloader.bytes_per_second_limit));
        MessageUtil.sendMessage(player, "world_downloader.response", path);
        return Command.SINGLE_SUCCESS;
    }

    public File compressRegionFile(ServerPlayer player) {
        /* get region location */
        ChunkPos chunkPos = player.chunkPosition();
        int regionX = chunkPos.getRegionX();
        int regionZ = chunkPos.getRegionZ();

        /* get world folder */
        ServerLevel world = player.serverLevel();
        MinecraftServer server = world.getServer();
        MinecraftServerAccessor serverAccess = (MinecraftServerAccessor) server;
        ResourceKey<Level> dimensionKey = world.dimension();
        LevelStorageSource.LevelStorageAccess session = serverAccess.getStorageSource();
        File worldDirectory = session.getDimensionPath(dimensionKey).toFile();

        /* compress file */
        String regionName = "r." + regionX + "." + regionZ + ".mca";
        File[] input = {
                new File(worldDirectory, "region" + File.separator + regionName),
                new File(worldDirectory, "poi" + File.separator + regionName),
                new File(worldDirectory, "entities" + File.separator + regionName)
        };
        File output;
        try {
            output = Files.createTempFile(regionName + "#", ".zip").toFile();
            compressFiles(input, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Generate region file: {}", output.getAbsolutePath());
        return output;
    }

    @SneakyThrows
    public void compressFiles(File[] input, File output) {
        try (FileOutputStream fos = new FileOutputStream(output);
             ArchiveOutputStream archiveOut = new ZipArchiveOutputStream(fos)) {
            for (File file : input) {
                if (file.isFile() && file.exists()) {
                    ArchiveEntry entry = new ZipArchiveEntry(file, getEntryName(file));
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

    private String getEntryName(File file) {
        return file.getParentFile().getName() + File.separator + file.getName();
    }

}
