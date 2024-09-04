package io.github.sakurawald.module.mixin.tab_list.faker;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.auxiliary.RandomUtil;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerCommonNetworkHandler.class)
public class FakePlayerPingMixin {

    @Inject(method = "getLatency", at = @At("HEAD"), cancellable = true)
    void fakePing(@NotNull CallbackInfoReturnable<Integer> cir) {
        int min = Configs.configHandler.model().modules.tab_list.faker.ping.min_ping;
        int max = Configs.configHandler.model().modules.tab_list.faker.ping.max_ping;
        int ping = RandomUtil.getRng().nextInt(min, max);
        cir.setReturnValue(ping);
    }

}
