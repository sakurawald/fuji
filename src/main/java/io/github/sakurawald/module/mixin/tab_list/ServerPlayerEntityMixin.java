package io.github.sakurawald.module.mixin.tab_list;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.afk.AfkStateAccessor;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.sakurawald.util.MessageUtil.ofText;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 + 250)
@Slf4j
public abstract class ServerPlayerEntityMixin {


    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    void modifyPlayerListName(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity target = player;
        String name = target.getGameProfile().getName();

        if (name.contains(TabListInitializer.META_SEPARATOR)) {
            name = name.substring(name.indexOf("@") + 1);

            log.warn("get name = {}", name);
            target = Fuji.SERVER.getPlayerManager().getPlayer(name);
            log.warn("target = {}", target);
        }

        cir.setReturnValue(ofText(target, false, Configs.configHandler.model().modules.tab_list.style.body));
    }
}
