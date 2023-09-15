package fun.sakurawald.module.top_chunks;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigGSON;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.mixin.top_chunks.ThreadedAnvilChunkStorageMixin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;

public class TopChunksModule {

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("chunks").executes(TopChunksModule::$chunks)
        );
    }

    private static int $chunks(CommandContext<CommandSourceStack> ctx) {
        CompletableFuture.runAsync(() -> {
            PriorityQueue<ChunkScore> PQ = new PriorityQueue<>();
            /* iter worlds */
            MinecraftServer server = ctx.getSource().getServer();
            for (ServerLevel world : server.getAllLevels()) {
                HashMap<ChunkPos, ChunkScore> chunkPos2ChunkScore = new HashMap<>();

                /* entity in this world */
                for (Entity entity : world.getAllEntities()) {
                    ChunkPos pos = entity.chunkPosition();
                    chunkPos2ChunkScore.putIfAbsent(pos, new ChunkScore(world, pos));
                    chunkPos2ChunkScore.get(pos).addEntity(entity);
                }

                /* block-entity in this world */
                ThreadedAnvilChunkStorageMixin threadedAnvilChunkStorage = (ThreadedAnvilChunkStorageMixin) world.getChunkSource().chunkMap;
                Iterable<ChunkHolder> chunkHolders = threadedAnvilChunkStorage.$getChunks();
                for (ChunkHolder chunkHolder : chunkHolders) {
                    LevelChunk worldChunk = chunkHolder.getTickingChunk();
                    if (worldChunk == null) continue;

                    /* count for block entities */
                    for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                        ChunkPos pos = worldChunk.getPos();
                        chunkPos2ChunkScore.putIfAbsent(pos, new ChunkScore(world, pos));
                        chunkPos2ChunkScore.get(pos).addBlockEntity(blockEntity);
                    }
                }

                /* add all ChunkScore in this world */
                chunkPos2ChunkScore.values().forEach(chunkScore -> {
                    chunkScore.sumUpScore();
                    PQ.add(chunkScore);
                });
            }

            /* send output */
            ConfigGSON.Modules.TopChunks topChunks = ConfigManager.configWrapper.instance().modules.top_chunks;
            calculateNearestPlayer(PQ, topChunks.rows * topChunks.columns);

            TextComponent.Builder textComponentBuilder = Component.text();
            outer:
            for (int j = 0; j < topChunks.rows; j++) {
                for (int i = 0; i < topChunks.columns; i++) {
                    if (PQ.isEmpty()) break outer;
                    textComponentBuilder.append(PQ.poll().asComponent(ctx.getSource())).append(Component.text(" "));
                }
                textComponentBuilder.append(Component.newline());
            }

            ctx.getSource().sendMessage(textComponentBuilder.asComponent());
        });

        return Command.SINGLE_SUCCESS;
    }

    private static void calculateNearestPlayer(PriorityQueue<ChunkScore> PQ, int limit) {
        int count = 0;
        for (ChunkScore chunkScore : PQ) {
            if (count++ >= limit) break;

            Level world = chunkScore.getWorld();
            ChunkPos chunkPos = chunkScore.getChunkPos();
            BlockPos blockPos = chunkPos.getWorldPosition();
            Player nearestPlayer = world.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ConfigManager.configWrapper.instance().modules.top_chunks.nearest_distance, false);
            if (nearestPlayer != null) {
                chunkScore.getPlayers().add(nearestPlayer.getGameProfile().getName() + " (nearest)");
            }
        }
    }
}
