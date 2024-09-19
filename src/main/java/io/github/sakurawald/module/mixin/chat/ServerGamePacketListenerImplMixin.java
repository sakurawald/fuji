package io.github.sakurawald.module.mixin.chat;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.chat.ChatInitializer;
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
    private static final ChatInitializer module = Managers.getModuleManager().getInitializer(ChatInitializer.class);

    @Shadow
    public ServerPlayerEntity player;

    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), argsOnly = true)
    public @NotNull SignedMessage modifyChatMessageSentByPlayers(@NotNull SignedMessage original) {
        String string = original.getContent().getString();

        if (module.config.getModel().spy.output_unparsed_message_into_console) {
            LogUtil.info("[chat spy] <{}> {}", player.getGameProfile().getName(), string);
        }

        Text text = module.parseText(player, string);
        return original.withUnsignedContent(text);
    }
}
