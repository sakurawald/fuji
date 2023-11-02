package io.github.sakurawald.module.initializer.top_chunks;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class ChunkScore implements Comparable<ChunkScore> {
    private final HashMap<String, Integer> type2amount = new HashMap<>();
    private final HashMap<String, String> type2transform_type = new HashMap<>() {
        {
            this.put("block.minecraft.mob_spawner", "block.minecraft.spawner");
            this.put("block.minecraft.brushable_block", "block.minecraft.suspicious_sand");
            this.put("block.minecraft.sign", "block.minecraft.oak_sign");
            this.put("block.minecraft.bed", "block.minecraft.white_bed");
            this.put("block.minecraft.skull", "block.minecraft.player_head");
            this.put("block.minecraft.banner", "block.minecraft.banner.base.white");
        }
    };

    @Getter
    private final Level dimension;
    @Getter
    private final ChunkPos chunkPos;
    @Getter
    private final ArrayList<String> players = new ArrayList<>();
    @Getter
    private int score;

    public ChunkScore(Level dimension, ChunkPos chunkPos) {
        this.dimension = dimension;
        this.chunkPos = chunkPos;
    }

    public void addEntity(Entity entity) {
        String type = entity.getType().getDescriptionId();
        type = type2transform_type.getOrDefault(type, type);

        type2amount.putIfAbsent(type, 0);
        type2amount.put(type, type2amount.get(type) + 1);

        if (entity instanceof ServerPlayer player) {
            this.players.add(player.getGameProfile().getName());
        }
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        ResourceLocation id = BlockEntityType.getKey(blockEntity.getType());
        if (id == null) return;

        // fix: add the prefix of BlockEntity
        String type = id.toLanguageKey("block");
        // fix: some block entity has an error translatable key, like mob_spawner
        type = type2transform_type.getOrDefault(type, type);
        type2amount.putIfAbsent(type, 0);
        type2amount.put(type, type2amount.get(type) + 1);
    }

    public void sumUpScore() {
        this.score = 0;
        for (String type : this.type2amount.keySet()) {
            HashMap<String, Integer> type2score = ConfigManager.configWrapper.instance().modules.top_chunks.type2score;
            this.score += type2score.getOrDefault(type, type2score.get("default")) * type2amount.get(type);
        }
    }

    @Override
    public String toString() {
        return String.format("%-5d", this.score);
    }

    @Override
    public int compareTo(@NotNull ChunkScore that) {
        return Integer.compare(that.score, this.score);
    }


    private Component formatTypes(CommandSourceStack source) {
        TextComponent.Builder ret = Component.text();
        this.type2amount.forEach((k, v) -> {
            Component component = MessageUtil.ofComponent(source, "top_chunks.prop.types.entry", v)
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[type]").replacement(Component.translatable(k)).build());
            ret.append(component);
        });
        return ret.asComponent();
    }

    public Component asComponent(CommandSourceStack source) {

        String chunkLocation;
        if (ConfigManager.configWrapper.instance().modules.top_chunks.hide_location) {
            chunkLocation = MessageUtil.ofString(source, "top_chunks.prop.hidden");
            if (source.hasPermission(4)) {
                chunkLocation = MessageUtil.ofString(source, "top_chunks.prop.hidden.bypass", this.getChunkPos().toString());
            }
        } else {
            chunkLocation = this.getChunkPos().toString();
        }

        Component hoverTextComponent = Component.text().color(NamedTextColor.GOLD)
                .append(MessageUtil.ofComponent(source, "top_chunks.prop.dimension", this.dimension.dimension().location()))
                .append(Component.newline())
                .append(MessageUtil.ofComponent(source, "top_chunks.prop.chunk", chunkLocation))
                .append(Component.newline())
                .append(MessageUtil.ofComponent(source, "top_chunks.prop.score", this.score))
                .append(Component.newline())
                .append(MessageUtil.ofComponent(source, "top_chunks.prop.players", this.players))
                .append(Component.newline())
                .append(MessageUtil.ofComponent(source, "top_chunks.prop.types"))
                .append(formatTypes(source)).build();
        return Component.text()
                .color(this.players.isEmpty() ? NamedTextColor.GRAY : NamedTextColor.DARK_GREEN)
                .append(Component.text(this.toString())).hoverEvent(HoverEvent.showText(hoverTextComponent)).build();
    }
}