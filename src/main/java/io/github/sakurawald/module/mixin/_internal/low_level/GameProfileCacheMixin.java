package io.github.sakurawald.module.mixin._internal.low_level;

import io.github.sakurawald.module.common.structure.GameProfileCacheEx;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.UserCache;

@Mixin(UserCache.class)
public class GameProfileCacheMixin implements GameProfileCacheEx {
    @Final
    @Shadow
    private Map<String, UserCache.Entry> byName;


    @Override
    public Collection<String> fuji$getNames() {
        ArrayList<String> ret = new ArrayList<>();
        byName.values().forEach(o -> ret.add(o.getProfile().getName()));
        return ret;
    }
}
