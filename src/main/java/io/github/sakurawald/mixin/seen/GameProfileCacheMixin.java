package io.github.sakurawald.mixin.seen;

import io.github.sakurawald.module.seen.GameProfileCacheEx;
import net.minecraft.server.players.GameProfileCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Mixin(GameProfileCache.class)
public class GameProfileCacheMixin implements GameProfileCacheEx {
    @Final
    @Shadow
    private Map<String, GameProfileCache.GameProfileInfo> profilesByName;


    @Override
    public Collection<String> fuji$getNames() {
        ArrayList<String> ret = new ArrayList<>();
        profilesByName.values().forEach(o -> ret.add(o.getProfile().getName()));
        return ret;
    }
}
