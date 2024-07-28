package io.github.sakurawald.module.mixin.command_toolbox.reply;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.command_toolbox.reply.ReplyInitializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(MessageCommand.class)
public class MsgCommandMixin {

    @Unique
    private static final ReplyInitializer module = Managers.getModuleManager().getInitializer(ReplyInitializer.class);

    @Inject(method = "execute", at = @At("HEAD"))
    private static void rememberRecentlyMessagedPlayer(@NotNull ServerCommandSource commandSourceStack, @NotNull Collection<ServerPlayerEntity> collection, SignedMessage playerChatMessage, CallbackInfo ci) {
        ServerPlayerEntity source = commandSourceStack.getPlayer();
        if (source == null) return;

        collection.forEach(target -> module.updateReplyTarget(target.getGameProfile().getName(), source.getGameProfile().getName()));
    }
}
