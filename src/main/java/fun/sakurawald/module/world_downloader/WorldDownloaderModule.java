package fun.sakurawald.module.world_downloader;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.mixin.resource_world.MinecraftServerAccessor;
import fun.sakurawald.util.MessageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class WorldDownloaderModule {

    private static final double BYTE_TO_MEGABYTE = 1.0 * 1024 * 1024;
    private static final EvictingQueue<String> CONTEXT_QUEUE = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.world_downloader.context_cache_size);
    private static HttpServer server;

    public static void initServer() {
        if (server != null) {
            server.stop(0);
        }

        try {
            server = HttpServer.create(new InetSocketAddress(ConfigManager.configWrapper.instance().modules.world_downloader.port), 0);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("download").executes(WorldDownloaderModule::$download));
    }

    public static void safelyRemoveContext(String path) {
        try {
            WorldDownloaderModule.server.removeContext(path);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    public static void safelyRemoveContext(HttpContext httpContext) {
        safelyRemoveContext(httpContext.getPath());
    }

    @SuppressWarnings("SameReturnValue")
    @SneakyThrows
    private static int $download(CommandContext<CommandSourceStack> ctx) {

        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }

        /* init server */
        if (server == null) {
            initServer();
        }

        /* remove redundant contexts */
        if (CONTEXT_QUEUE.remainingCapacity() == 0) {
            log.info("contexts is full, remove the oldest context. {}", CONTEXT_QUEUE.peek());
            safelyRemoveContext(CONTEXT_QUEUE.poll());
        }

        /* create context */
        String path = "/download/" + UUID.randomUUID();
        CONTEXT_QUEUE.add(path);
        File file = compressRegionFile(player);
        MessageUtil.sendBroadcast("world_downloader.request", player.getGameProfile().getName(), file.length() / BYTE_TO_MEGABYTE);
        server.createContext(path, new FileDownloadHandler(file, ConfigManager.configWrapper.instance().modules.world_downloader.bytes_per_second_limit));
        MessageUtil.sendMessage(player, "world_downloader.response", path);
        return Command.SINGLE_SUCCESS;
    }

    public static File compressRegionFile(ServerPlayer player) {
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
    public static void compressFiles(File[] input, File output) {
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

    private static String getEntryName(File file) {
        return file.getParentFile().getName() + File.separator + file.getName();
    }
}
