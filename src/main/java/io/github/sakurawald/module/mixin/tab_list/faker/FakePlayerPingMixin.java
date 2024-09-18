package io.github.sakurawald.module.mixin.tab_list.faker;

import io.github.sakurawald.core.auxiliary.RandomUtil;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.tab_list.faker.TabListFakerInitializer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerCommonNetworkHandler.class)
public class FakePlayerPingMixin {

    @Unique
    private static final TabListFakerInitializer initializer = Managers.getModuleManager().getInitializer(TabListFakerInitializer.class);

    @Inject(method = "getLatency", at = @At("HEAD"), cancellable = true)
    void fakePing(@NotNull CallbackInfoReturnable<Integer> cir) {
        int min = initializer.config.getModel().ping.min_ping;
        int max = initializer.config.getModel().ping.max_ping;
        int ping = RandomUtil.getRandom().nextInt(min, max);
        cir.setReturnValue(ping);
    }

}
