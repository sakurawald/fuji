package io.github.sakurawald.core.command.structure;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtendedCommandSource {
    ServerCommandSource source;
    boolean parsePlaceholder;

    public ExtendedCommandSource modifySource(Function<ServerCommandSource, ServerCommandSource> modifier) {
        this.source = modifier.apply(this.source);
        return this;
    }

    public String processCommand(String string) {
        if (!this.parsePlaceholder) return string;

        ServerPlayerEntity contextualPlayer = this.source.getPlayer();
        if (contextualPlayer != null) {
            string = LocaleHelper.resolvePlaceholder(contextualPlayer, string);
        } else {
            string = LocaleHelper.resolvePlaceholder(ServerHelper.getDefaultServer(), string);
        }

        return string;
    }

    public static ExtendedCommandSource ofSource(@NotNull ServerCommandSource source, boolean parsePlaceholder) {
        return new ExtendedCommandSource(source, parsePlaceholder);
    }

    public static ExtendedCommandSource ofPlayer(@NotNull PlayerEntity player, boolean parsePlaceholder) {
        return new ExtendedCommandSource(player.getCommandSource(), parsePlaceholder);
    }

    public static ExtendedCommandSource ofServer(boolean parsePlaceholder) {
        ServerCommandSource commandSource = ServerHelper.getDefaultServer().getCommandSource();
        return new ExtendedCommandSource(commandSource, parsePlaceholder);
    }

    public static ExtendedCommandSource ofSource(@NotNull ServerCommandSource source) {
        return ofSource(source, true);
    }

    public static ExtendedCommandSource ofPlayer(@NotNull PlayerEntity player) {
        return ofPlayer(player, true);
    }

    public static ExtendedCommandSource ofServer() {
        return ofServer(true);
    }
}
