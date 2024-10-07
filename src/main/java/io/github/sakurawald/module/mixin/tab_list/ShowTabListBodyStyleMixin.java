package io.github.sakurawald.module.mixin.tab_list;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.sakurawald.core.auxiliary.RandomUtil;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerPlayerEntity.class)
public abstract class ShowTabListBodyStyleMixin {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    @NotNull
    Text modifyPlayerListName(@Nullable Text original) {
        // respect other's modification.
        if (original == null) {
            return LocaleHelper.getTextByValue(player, RandomUtil.drawList(TabListInitializer.config.model().style.body));
        }

        return original;
    }
}
