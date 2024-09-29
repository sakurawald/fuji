package io.github.sakurawald.module.initializer.top_chunks;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.top_chunks.config.model.TopChunksConfigModel;
import io.github.sakurawald.module.initializer.top_chunks.structure.ChunkScore;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;


public class TopChunksInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<TopChunksConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, TopChunksConfigModel.class);

    @CommandNode("chunks")
    private static int $chunks(@CommandSource CommandContext<ServerCommandSource> ctx) {
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
            var config = TopChunksInitializer.config.getModel();
            computeNearestPlayer(ctx.getSource(), PQ, config.top.rows * config.top.columns);

            MutableText textComponentBuilder = Text.empty();
            outer:
            for (int j = 0; j < config.top.rows; j++) {
                for (int i = 0; i < config.top.columns; i++) {
                    if (PQ.isEmpty()) break outer;
                    textComponentBuilder.append(PQ.poll().asText(ctx.getSource())).append(LocaleHelper.TEXT_SPACE);
                }
                textComponentBuilder.append(LocaleHelper.TEXT_NEWLINE);
            }

            ctx.getSource().sendMessage(textComponentBuilder);

            if (ctx.getSource().getPlayer() != null && ChunkScore.hasPermissionToClickToTeleport(ctx.getSource().getPlayer())) {
                LocaleHelper.sendMessageByKey(ctx.getSource(), "prompt.click.teleport");
            }

        });

        return CommandHelper.Return.SUCCESS;
    }

    private static void computeNearestPlayer(ServerCommandSource source, @NotNull PriorityQueue<ChunkScore> PQ, int limit) {
        int count = 0;
        for (ChunkScore chunkScore : PQ) {
            if (count++ >= limit) break;

            World world = chunkScore.getDimension();
            ChunkPos chunkPos = chunkScore.getChunkPos();
            BlockPos blockPos = chunkPos.getStartPos();
            PlayerEntity nearestPlayer = world.getClosestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), config.getModel().nearest_distance, false);
            if (nearestPlayer != null) {
                chunkScore.getPlayers().add(LocaleHelper.getValue(source, "top_chunks.prop.players.nearest", nearestPlayer.getGameProfile().getName()));
            }
        }
    }

}
