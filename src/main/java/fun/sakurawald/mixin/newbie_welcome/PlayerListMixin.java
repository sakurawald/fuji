package fun.sakurawald.mixin.newbie_welcome;

import fun.sakurawald.module.ModuleManager;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.newbie_welcome.NewbieWelcomeModule;
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
    private static final NewbieWelcomeModule module = ModuleManager.getOrNewInstance(NewbieWelcomeModule.class);
    @Unique
    private static final BetterFakePlayerModule betterFakePlayerModule = ModuleManager.getOrNewInstance(BetterFakePlayerModule.class);

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        if (betterFakePlayerModule.isFakePlayer(player)) return;
        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) < 1) {
            module.welcomeNewbiePlayer(player);
        }
    }
}
