package io.github.sakurawald.module.mixin.chat;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.chat.ChatInitializer;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.message.MessageSignatureStorage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1001)
@Slf4j
public abstract class ServerGamePacketListenerImplMixin {

    @Unique
    private static final ChatInitializer module = ModuleManager.getInitializer(ChatInitializer.class);
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "handleDecoratedMessage", at = @At(value = "HEAD"), cancellable = true)
    public void handleChat(SignedMessage playerChatMessage, CallbackInfo ci) {
        Component component = module.parseChatComponent(player, playerChatMessage.getContent().getString());
        module.getChatHistory().add(component);

        // info so that it can be seen in the console
        Fuji.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(component));

        for (ServerPlayerEntity serverPlayerEntity : Fuji.SERVER.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.sendMessage(component);
        }

        ci.cancel();
    }
}
