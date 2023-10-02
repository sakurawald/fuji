package fun.sakurawald.mixin.better_fake_player;

import fun.sakurawald.module.ModuleManager;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Unique
    private static final BetterFakePlayerModule module = ModuleManager.getOrNewInstance(BetterFakePlayerModule.class);

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        if (module.isFakePlayer(player)) return;
        if (module.hasFakePlayers(player)) {
            module.renewFakePlayers(player);
        }
    }
}
