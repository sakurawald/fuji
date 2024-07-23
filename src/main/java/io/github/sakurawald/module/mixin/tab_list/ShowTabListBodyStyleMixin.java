package io.github.sakurawald.module.mixin.tab_list;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.sakurawald.config.Configs;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.sakurawald.util.MessageUtil.ofText;

@Mixin(value = ServerPlayerEntity.class)
@Slf4j
public abstract class ShowTabListBodyStyleMixin {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    Text modifyPlayerListName(Text original) {
        log.warn("{} -> getPlayerListName = {}", "tablist module", original);

        // respect other's modification.
        if (original == null) {
            return ofText(player, false, Configs.configHandler.model().modules.tab_list.style.body);
        }

        return original;
    }
}
