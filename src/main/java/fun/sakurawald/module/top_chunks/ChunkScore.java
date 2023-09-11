package fun.sakurawald.module.top_chunks;

import fun.sakurawald.config.ConfigManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkScore implements Comparable<ChunkScore> {
    private final HashMap<String, Integer> type2amount = new HashMap<>();

    @Getter
    private final Level world;
    @Getter
    private final ChunkPos chunkPos;
    @Getter
    private final ArrayList<String> players = new ArrayList<>();
    @Getter
    private int score;

    public ChunkScore(Level world, ChunkPos chunkPos) {
        this.world = world;
        this.chunkPos = chunkPos;
    }

    public void addEntity(Entity entity) {
        String type = EntityType.getKey(entity.getType()).getPath();
        type2amount.putIfAbsent(type, 0);
        type2amount.put(type, type2amount.get(type) + 1);

        if (entity instanceof ServerPlayer player) {
            this.players.add(player.getGameProfile().getName());
        }
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        ResourceLocation id = BlockEntityType.getKey(blockEntity.getType());
        if (id == null) return;

        String type = id.getPath();
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

    public Component asComponent(CommandSourceStack source) {

        String chunkLocation;
        if (ConfigManager.configWrapper.instance().modules.top_chunks.hide_location) {
            chunkLocation = "hidden";
            if (source.hasPermission(4)) {
                chunkLocation = this.getChunkPos().toString() + " (bypass-hidden)";
            }
        } else {
            chunkLocation = this.getChunkPos().toString();
        }

        Component hoverTextComponent = Component.text().color(NamedTextColor.GOLD)
                .append(Component.text(String.format("World: %s", this.world.dimensionTypeId().location())))
                .append(Component.newline())
                .append(Component.text("Chunk: " + chunkLocation))
                .append(Component.newline())
                .append(Component.text(String.format("Score: %d", this.score)))
                .append(Component.newline())
                .append(Component.text(String.format("Players: %s", this.players)))
                .append(Component.newline())
                .append(Component.text(String.format("Types: %s", this.type2amount))).build();
        return Component.text()
                .color(this.players.isEmpty() ? NamedTextColor.GRAY : NamedTextColor.DARK_GREEN)
                .append(Component.text(this.toString())).hoverEvent(HoverEvent.showText(hoverTextComponent)).build();
    }
}