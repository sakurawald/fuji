package io.github.sakurawald.module.mixin.chat;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.chat.ChatInitializer;
import io.github.sakurawald.util.LogUtil;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1000 + 1)
public abstract class ServerGamePacketListenerImplMixin {

    @Unique
    private static final ChatInitializer module = ModuleManager.getInitializer(ChatInitializer.class);
    @Shadow
    public ServerPlayerEntity player;


    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), argsOnly = true)
    public @NotNull SignedMessage modifyChatMessageSentByPlayers(@NotNull SignedMessage before) {
        if (Configs.configHandler.model().modules.chat.spy.output_unparsed_message_into_console) {
            LogUtil.info("[Chat Spy] <{}> {}", player.getGameProfile().getName(), before.getContent().getString());
        }

        Text text = module.parseText(player, before.getContent().getString());

        module.getChatHistory().add(text.asComponent());
        return before.withUnsignedContent(text);
    }
}
