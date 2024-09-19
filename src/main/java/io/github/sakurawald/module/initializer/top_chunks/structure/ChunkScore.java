package io.github.sakurawald.module.initializer.top_chunks.structure;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.TypeFormatter;
import io.github.sakurawald.module.initializer.top_chunks.TopChunksInitializer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChunkScore implements Comparable<ChunkScore> {
    private final HashMap<String, Integer> type2amount = new HashMap<>();

    @Getter
    private final World dimension;
    @Getter
    private final ChunkPos chunkPos;
    @Getter
    private final List<String> players = new ArrayList<>();
    @Getter
    private int score;

    public ChunkScore(World dimension, ChunkPos chunkPos) {
        this.dimension = dimension;
        this.chunkPos = chunkPos;
    }

    public void addEntity(@NotNull Entity entity) {
        String type = entity.getType().getTranslationKey();
        type = TypeFormatter.type2transform_type.getOrDefault(type, type);

        type2amount.putIfAbsent(type, 0);
        type2amount.put(type, type2amount.get(type) + 1);

        if (entity instanceof ServerPlayerEntity player) {
            this.players.add(player.getGameProfile().getName());
        }
    }

    public void addBlockEntity(@NotNull BlockEntity blockEntity) {
        Identifier id = BlockEntityType.getId(blockEntity.getType());
        if (id == null) return;

        // fix: add the prefix of BlockEntity
        String type = id.toTranslationKey("block");
        // fix: some block entity has an error translatable key, like mob_spawner
        type = TypeFormatter.type2transform_type.getOrDefault(type, type);
        type2amount.putIfAbsent(type, 0);
        type2amount.put(type, type2amount.get(type) + 1);
    }

    public void sumUpScore() {
        this.score = 0;
        for (String type : this.type2amount.keySet()) {
            HashMap<String, Integer> type2score = TopChunksInitializer.config.getModel().type2score;
            this.score += type2score.getOrDefault(type, type2score.get("default")) * type2amount.get(type);
        }
    }

    @Override
    public @NotNull String toString() {
        return String.format("%-5d", this.score);
    }

    @Override
    public int compareTo(@NotNull ChunkScore that) {
        return Integer.compare(that.score, this.score);
    }

    public @NotNull Component asComponent(@NotNull ServerCommandSource source) {

        String chunkLocation;
        if (TopChunksInitializer.config.getModel().hide_location) {
            chunkLocation = LocaleHelper.getValue(source, "top_chunks.prop.hidden");
            if (source.hasPermissionLevel(4)) {
                chunkLocation = LocaleHelper.getValue(source, "top_chunks.prop.hidden.bypass", this.getChunkPos().toString());
            }
        } else {
            chunkLocation = this.getChunkPos().toString();
        }

        Component hoverTextComponent = Component.text().color(NamedTextColor.GOLD)
                .append(LocaleHelper.getTextByKey(source, "top_chunks.prop.dimension", this.dimension.getRegistryKey().getValue()))
                .append(Component.newline())
                .append(LocaleHelper.getTextByKey(source, "top_chunks.prop.chunk", chunkLocation))
                .append(Component.newline())
                .append(LocaleHelper.getTextByKey(source, "top_chunks.prop.score", this.score))
                .append(Component.newline())
                .append(LocaleHelper.getTextByKey(source, "top_chunks.prop.players", this.players))
                .append(Component.newline())
                .append(LocaleHelper.getTextByKey(source, "top_chunks.prop.types"))
                .append(TypeFormatter.formatTypes(source, this.type2amount)).build();
        return Component.text()
                .color(this.players.isEmpty() ? NamedTextColor.GRAY : NamedTextColor.DARK_GREEN)
                .append(Component.text(this.toString())).hoverEvent(HoverEvent.showText(hoverTextComponent)).build();
    }
}
