package io.github.sakurawald.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.seen.GameProfileCacheEx;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.players.GameProfileCache;

import static net.minecraft.commands.Commands.argument;

@UtilityClass
public class CommandUtil {
    public static RequiredArgumentBuilder<CommandSourceStack, String> offlinePlayerArgument(String argumentName) {
        return argument(argumentName, StringArgumentType.string())
                .suggests((context, builder) -> {
                            GameProfileCache gameProfileCache = ServerMain.SERVER.getProfileCache();
                            if (gameProfileCache != null) {
                                ((GameProfileCacheEx) gameProfileCache).fuji$getNames().forEach(builder::suggest);
                            }
                            return builder.buildFuture();
                        }
                );
    }


    public static RequiredArgumentBuilder<CommandSourceStack, String> offlinePlayerArgument() {
        return offlinePlayerArgument("player");
    }
}
