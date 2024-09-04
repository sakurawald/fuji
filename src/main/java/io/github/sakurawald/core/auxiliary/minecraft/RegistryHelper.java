package io.github.sakurawald.core.auxiliary.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.registry.RegistryWrapper;

@UtilityClass
public class RegistryHelper {
    public static RegistryWrapper.WrapperLookup getDefaultWrapperLookup(){
        return ServerHelper.getDefaultServer().getRegistryManager();
    }
}
