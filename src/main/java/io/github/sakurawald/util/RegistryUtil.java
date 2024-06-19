package io.github.sakurawald.util;

import io.github.sakurawald.Fuji;
import lombok.experimental.UtilityClass;
import net.minecraft.registry.RegistryWrapper;

@UtilityClass
public class RegistryUtil {
    public static RegistryWrapper.WrapperLookup getDefaultWrapperLookup(){
        return Fuji.SERVER.getRegistryManager();
    }
}
