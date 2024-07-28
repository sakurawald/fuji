package io.github.sakurawald.module.mixin._internal.low_level;

import io.github.sakurawald.module.common.accessor.GameProfileCacheEx;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.UserCache;

@Mixin(UserCache.class)
public class UserCacheMixin implements GameProfileCacheEx {
    @Final
    @Shadow
    private Map<String, UserCache.Entry> byName;

    @Override
    public @NotNull Collection<String> fuji$getNames() {
        ArrayList<String> ret = new ArrayList<>();
        byName.values().forEach(o -> ret.add(o.getProfile().getName()));
        return ret;
    }
}
