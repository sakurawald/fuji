package io.github.sakurawald.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.sakurawald.ServerMain;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.players.GameProfileCache;
import java.util.List;
import static net.minecraft.commands.Commands.argument;

@UtilityClass
public class CommandUtil {
    public static RequiredArgumentBuilder<CommandSourceStack, String> offlinePlayerArgument() {
        return argument("player", StringArgumentType.string())
                .suggests((context, builder) -> {
                            GameProfileCache profileCache = ServerMain.SERVER.getProfileCache();
                            if (profileCache != null) {
                                List<GameProfileCache.GameProfileInfo> load = profileCache.load();
                                load.forEach(gameProfileInfo -> builder.suggest(gameProfileInfo.getProfile().getName()));
                            }
                            return builder.buildFuture();
                        }
                );
    }
}
