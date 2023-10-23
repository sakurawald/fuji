package io.github.sakurawald.mixin.chat;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.chat.ChatStyleModule;
import io.github.sakurawald.util.CarpetUtil;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, priority = 999)
public abstract class PlayerListMixin {

    @Unique
    private static final ChatStyleModule module = ModuleManager.getOrNewInstance(ChatStyleModule.class);

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        if (CarpetUtil.isFakePlayer(player)) return;
        module.getChatHistory().forEach(player::sendMessage);
    }
}
