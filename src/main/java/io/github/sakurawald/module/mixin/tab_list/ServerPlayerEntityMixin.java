package io.github.sakurawald.module.mixin.tab_list;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.afk.AfkStateAccessor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.sakurawald.util.MessageUtil.ofText;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 + 250)
public class ServerPlayerEntityMixin {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    void f(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(ofText(player, false, Configs.configHandler.model().modules.tab_list.style.body));
    }

}
