package io.github.sakurawald.module.initializer.command_toolbox.nickname;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.TextHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.nickname.config.model.NicknameConfigModel;
import io.github.sakurawald.module.initializer.command_toolbox.nickname.config.model.NicknameDataModel;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandNode("nickname")
public class NicknameInitializer extends ModuleInitializer {

    @Getter
    private static final BaseConfigurationHandler<NicknameDataModel> data = new ObjectConfigurationHandler<>("nickname.json", NicknameDataModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("nickname.json"), NicknameInitializer.class));

    private static final BaseConfigurationHandler<NicknameConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, NicknameConfigModel.class);

    private static String transformNickname(String string) {
        return config.model().transform_nickname.formatted(string);
    }

    @CommandNode("set")
    private static int $set(@CommandSource ServerPlayerEntity player, GreedyString format) {
        String name = player.getGameProfile().getName();
        String value = format.getValue();

        // transform nickname
        value = transformNickname(value);

        data.model().format.player2format.put(name, value);
        data.writeStorage();

        TextHelper.sendMessageByKey(player, "nickname.set");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("reset")
    private static int $reset(@CommandSource ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        data.model().format.player2format.remove(name);
        data.writeStorage();

        TextHelper.sendMessageByKey(player, "nickname.unset");
        return CommandHelper.Return.SUCCESS;
    }
}
