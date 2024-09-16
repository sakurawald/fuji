package io.github.sakurawald.module.initializer.top_chunks;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.top_chunks.structure.ChunkScore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;


public class TopChunksInitializer extends ModuleInitializer {


    @CommandNode("chunks")
    private int $chunks(@CommandSource CommandContext<ServerCommandSource> ctx) {
        CompletableFuture.runAsync(() -> {
            PriorityQueue<ChunkScore> PQ = new PriorityQueue<>();
            /* iter worlds */
            MinecraftServer server = ctx.getSource().getServer();
            for (ServerWorld world : server.getWorlds()) {
                HashMap<ChunkPos, ChunkScore> chunkPos2ChunkScore = new HashMap<>();

                /* entity in this world */
                for (Entity entity : world.iterateEntities()) {
                    ChunkPos pos = entity.getChunkPos();
                    chunkPos2ChunkScore.putIfAbsent(pos, new ChunkScore(world, pos));
                    chunkPos2ChunkScore.get(pos).addEntity(entity);
                }

                /* block-entity in this world */
                Iterable<ChunkHolder> chunkHolders = world.getChunkManager().chunkLoadingManager.entryIterator();
                for (ChunkHolder chunkHolder : chunkHolders) {
                    WorldChunk worldChunk = chunkHolder.getWorldChunk();
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
            ConfigModel.Modules.TopChunks topChunks = Configs.configHandler.model().modules.top_chunks;
            calculateNearestPlayer(ctx.getSource(), PQ, topChunks.top.rows * topChunks.top.columns);

            TextComponent.Builder textComponentBuilder = Component.text();
            outer:
            for (int j = 0; j < topChunks.top.rows; j++) {
                for (int i = 0; i < topChunks.top.columns; i++) {
                    if (PQ.isEmpty()) break outer;
                    textComponentBuilder.append(PQ.poll().asComponent(ctx.getSource())).appendSpace();
                }
                textComponentBuilder.append(Component.newline());
            }

            ctx.getSource().sendMessage(textComponentBuilder.asComponent());
        });

        return CommandHelper.Return.SUCCESS;
    }

    private void calculateNearestPlayer(ServerCommandSource source, @NotNull PriorityQueue<ChunkScore> PQ, int limit) {
        int count = 0;
        for (ChunkScore chunkScore : PQ) {
            if (count++ >= limit) break;

            World world = chunkScore.getDimension();
            ChunkPos chunkPos = chunkScore.getChunkPos();
            BlockPos blockPos = chunkPos.getStartPos();
            PlayerEntity nearestPlayer = world.getClosestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Configs.configHandler.model().modules.top_chunks.nearest_distance, false);
            if (nearestPlayer != null) {
                chunkScore.getPlayers().add(LocaleHelper.getValue(source, "top_chunks.prop.players.nearest", nearestPlayer.getGameProfile().getName()));
            }
        }
    }

}
