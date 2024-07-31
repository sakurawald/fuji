package io.github.sakurawald.module.initializer.command_toolbox.nickname;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.NicknameModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Command("nickname")
public class NicknameInitializer extends ModuleInitializer {

    @Getter
    private static final ConfigHandler<NicknameModel> nicknameHandler = new ObjectConfigHandler<>("nickname.json", NicknameModel.class);

    @Override
    public void onInitialize() {
        nicknameHandler.loadFromDisk();
    }

    @Command("set")
    private int $set(@CommandSource ServerPlayerEntity player, GreedyString format) {
            String name = player.getGameProfile().getName();
            nicknameHandler.model().format.player2format.put(name, format.getString());
            nicknameHandler.saveToDisk();

            MessageHelper.sendMessage(player, "nickname.set");
            return CommandHelper.Return.SUCCESS;
    }

    @Command("reset")
    private int $reset(@CommandSource ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        nicknameHandler.model().format.player2format.remove(name);
        nicknameHandler.saveToDisk();

        MessageHelper.sendMessage(player, "nickname.unset");
        return CommandHelper.Return.SUCCESS;
    }
}
