package fun.sakurawald.mixin.chat_style;

import fun.sakurawald.module.ModuleManager;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.chat_style.ChatStyleModule;
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

    @Unique
    private static final BetterFakePlayerModule betterFakePlayerModule = ModuleManager.getOrNewInstance(BetterFakePlayerModule.class);

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        if (betterFakePlayerModule.isFakePlayer(player)) return;
        module.getChatHistory().forEach(player::sendMessage);
    }
}
