package io.github.sakurawald.module.mixin.newbie_welcome;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.newbie_welcome.NewbieWelcomeModule;
import io.github.sakurawald.util.CarpetUtil;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Unique
    private static final NewbieWelcomeModule module = ModuleManager.getInitializer(NewbieWelcomeModule.class);

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        if (CarpetUtil.isFakePlayer(player)) return;
        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) < 1) {
            module.welcomeNewbiePlayer(player);
        }
    }
}