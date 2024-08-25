package io.github.sakurawald.module.mixin.tab_list;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.auxiliary.RandomUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.sakurawald.auxiliary.minecraft.MessageHelper.ofText;

@Mixin(value = ServerPlayerEntity.class)
public abstract class ShowTabListBodyStyleMixin {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    @NotNull
    Text modifyPlayerListName(@Nullable Text original) {
        // respect other's modification.
        if (original == null) {
            return ofText(player, false, RandomUtil.drawList(Configs.configHandler.model().modules.tab_list.style.body));
        }

        return original;
    }
}
