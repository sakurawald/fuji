package io.github.sakurawald.module.initializer.command_toolbox.nickname;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.handler.interfaces.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.command_toolbox.nickname.config.model.NicknameModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandNode("nickname")
public class NicknameInitializer extends ModuleInitializer {

    @Getter
    private static final ConfigHandler<NicknameModel> nicknameHandler = new ObjectConfigHandler<>("nickname.json", NicknameModel.class);

    @Override
    public void onInitialize() {
        nicknameHandler.loadFromDisk();
    }

    @CommandNode("set")
    private int $set(@CommandSource ServerPlayerEntity player, GreedyString format) {
            String name = player.getGameProfile().getName();
            nicknameHandler.model().format.player2format.put(name, format.getString());
            nicknameHandler.saveToDisk();

            MessageHelper.sendMessage(player, "nickname.set");
            return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("reset")
    private int $reset(@CommandSource ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        nicknameHandler.model().format.player2format.remove(name);
        nicknameHandler.saveToDisk();

        MessageHelper.sendMessage(player, "nickname.unset");
        return CommandHelper.Return.SUCCESS;
    }
}
