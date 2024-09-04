package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class TypeFormatter {

    public static final HashMap<String, String> type2transform_type = new HashMap<>() {
        {
            this.put("block.minecraft.mob_spawner", "block.minecraft.spawner");
            this.put("block.minecraft.brushable_block", "block.minecraft.suspicious_sand");
            this.put("block.minecraft.sign", "block.minecraft.oak_sign");
            this.put("block.minecraft.bed", "block.minecraft.white_bed");
            this.put("block.minecraft.skull", "block.minecraft.player_head");
            this.put("block.minecraft.banner", "block.minecraft.banner.base.white");
        }
    };

    public static @NotNull Component formatTypes(ServerCommandSource source, Map<String, Integer> type2amount) {
        TextComponent.Builder ret = Component.text();
        type2amount.forEach((k, v) -> {
            Component component = MessageHelper.ofComponent(source, "types.entry", v)
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[type]").replacement(Component.translatable(k)).build());
            ret.append(component);
        });
        return ret.asComponent();
    }

}
