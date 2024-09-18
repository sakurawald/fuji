package io.github.sakurawald.module.mixin.chat;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.sakurawald.core.accessor.SimpleRegistryAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.text.Decoration;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings({"unchecked"})
@Mixin(value = RegistryLoader.class)
public class RegistryLoaderMixin {

    /**
     * This amazing mixin is written by Patbox.
     * And the source is inside: <a href="https://github.dev/Patbox/StyledChat/blob/1.21/src/main/java/eu/pb4/styledchat/mixin/MessageArgumentTypeMixin.java">
     * Thanks to his great work.
     * <p>
     * I modified the code to override the vanilla MessageType.CHAT format.
     *
     * @see net.minecraft.registry.SimpleRegistry#add(net.minecraft.registry.RegistryKey, java.lang.Object, net.minecraft.registry.entry.RegistryEntryInfo)
     */
    @Inject(method = "load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;"
        , at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.AFTER))
    private static void modifyTheVanillaChatFormat(@Coerce Object registryLoadable, DynamicRegistryManager dynamicRegistryManager, List<RegistryLoader.Entry<?>> entries, CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir,
                                                   @Local(ordinal = 1) @NotNull List<RegistryLoader.Loader<?>> loaders) {
        Decoration firstDecoration = new Decoration("%s", List.of(Decoration.Parameter.CONTENT), Style.EMPTY);
        Decoration secondDecoration = Decoration.ofChat("chat.type.text.narrate");

        for (RegistryLoader.Loader<?> entry : loaders) {
            MutableRegistry<?> registry = entry.comp_2246();
            RegistryKey<? extends Registry<?>> registryKey = registry.getKey();

            if (registryKey.equals(RegistryKeys.MESSAGE_TYPE)) {
                Registry<MessageType> registryForMessageType = (Registry<MessageType>) registry;

                // The code is tricky, we need to register it later to override the vanilla registerKey.
                ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                    SimpleRegistryAccessor<MessageType> ex = (SimpleRegistryAccessor<MessageType>) registry;
                    ex.fuji$setFrozen(false);
                    Registry.register(registryForMessageType, MessageType.CHAT, new MessageType(firstDecoration, secondDecoration));
                    ex.fuji$setFrozen(true);
                });

            }
        }
    }
}
