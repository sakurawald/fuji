package io.github.sakurawald.util.minecraft;

import io.github.sakurawald.Fuji;
import lombok.experimental.UtilityClass;
import net.minecraft.registry.RegistryWrapper;

@UtilityClass
public class RegistryHelper {
    public static RegistryWrapper.WrapperLookup getDefaultWrapperLookup(){
        return Fuji.SERVER.getRegistryManager();
    }
}
