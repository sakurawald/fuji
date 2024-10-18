package io.github.sakurawald.module.initializer.chat.style;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.style.model.ChatFormatModel;
import io.github.sakurawald.module.initializer.chat.style.model.ChatStyleConfigModel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChatStyleInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<ChatStyleConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, ChatStyleConfigModel.class);

    // to avoid the message type already registered in the client-side, and the client-side message type will influence the client-side decorator.
    public static final RegistryKey<MessageType> MESSAGE_TYPE_KEY = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, Identifier.ofVanilla("fuji_chat_" + FabricLoader.getInstance().getEnvironmentType().toString().toLowerCase()));

    public static final MessageType MESSAGE_TYPE_VALUE = new MessageType(
        Decoration.ofChat("%s%s"),
        Decoration.ofChat("%s%s"));

    private static final BaseConfigurationHandler<ChatFormatModel> chat = new ObjectConfigurationHandler<>("chat.json", ChatFormatModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("chat.json"), ChatStyleInitializer.class));

    @CommandNode("chat style set")
    private static int setPlayerFormat(@CommandSource ServerPlayerEntity player, GreedyString format) {
        /* save the format*/
        String name = player.getGameProfile().getName();
        String $format = format.getValue();
        chat.model().format.player2format.put(name, $format);
        chat.writeStorage();

        /* feedback */
        $format = LocaleHelper.getValue(player, "chat.format.set").replace("%s", $format);
        $format = $format.replace("%message%", LocaleHelper.getValue(player, "chat.format.show"));
        Text text = LocaleHelper.getTextByValue(null, $format);

        player.sendMessage(text);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("chat style reset")
    private static int resetPlayerFormat(@CommandSource ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        chat.model().format.player2format.remove(name);
        chat.writeStorage();
        LocaleHelper.sendMessageByKey(player, "chat.format.reset");
        return CommandHelper.Return.SUCCESS;
    }


    public static @NotNull Text parseSenderText(@NotNull ServerPlayerEntity player) {
        String senderString = config.model().style.sender;
        return LocaleHelper.getTextByValue(player, senderString);
    }

    public static @NotNull Text parseContentText(@NotNull ServerPlayerEntity player, String message) {
        String contentString = config.model().style.content.formatted(message);

        contentString = chat.model().format.player2format.getOrDefault(player.getGameProfile().getName(), "%message%").replace("%message%", contentString);

        return LocaleHelper.getTextByValue(player, contentString);
    }

}
