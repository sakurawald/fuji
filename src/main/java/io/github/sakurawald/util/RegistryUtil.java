package io.github.sakurawald.util;

import io.github.sakurawald.Fuji;
import net.minecraft.registry.RegistryWrapper;

public class RegistryUtil {
    public static RegistryWrapper.WrapperLookup getDefaultWrapperLookup(){
        return Fuji.SERVER.getRegistryManager();
    }
}
