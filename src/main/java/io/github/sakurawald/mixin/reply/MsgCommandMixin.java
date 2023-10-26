package io.github.sakurawald.mixin.reply;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.reply.ReplyModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Unique
    private static final ReplyModule module = ModuleManager.getOrNewInstance(ReplyModule.class);

    @Inject(method = "sendMessage", at = @At("HEAD"))
    private static void sendMessage(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, PlayerChatMessage playerChatMessage, CallbackInfo ci) {
        ServerPlayer source = commandSourceStack.getPlayer();
        if (source == null) return;

        collection.forEach(target -> module.updateReplyTarget(target.getGameProfile().getName(), source.getGameProfile().getName()));
    }
}
