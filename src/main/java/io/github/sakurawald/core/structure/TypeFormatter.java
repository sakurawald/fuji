package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import lombok.experimental.UtilityClass;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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

    public static @NotNull Text formatTypes(ServerCommandSource source, Map<String, Integer> type2amount) {
        MutableText ret = Text.empty();
        type2amount.forEach((k, v) -> {
            Text text = LocaleHelper.getTextByKey(source, "types.entry", v);
            text = LocaleHelper.replaceBracketedText(text, "[type]", Text.translatable(k));
            ret.append(text);
        });
        return ret;
    }

}
