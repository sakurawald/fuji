package io.github.sakurawald.module.mixin.tab_list.faker;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.NumberUtil;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {

    @Inject(method = "getLatency", at = @At("HEAD"), cancellable = true)
    void fakePing(CallbackInfoReturnable<Integer> cir) {
        int min = Configs.configHandler.model().modules.tab_list.faker.ping.min_ping;
        int max = Configs.configHandler.model().modules.tab_list.faker.ping.max_ping;
        int ping = NumberUtil.getRng().nextInt(min, max);
        cir.setReturnValue(ping);
    }

}
