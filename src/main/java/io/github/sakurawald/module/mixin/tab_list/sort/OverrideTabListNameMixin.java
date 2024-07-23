package io.github.sakurawald.module.mixin.tab_list.sort;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.sakurawald.util.MessageUtil.ofText;

// the last to check the return value
@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 125)
@Slf4j
public abstract class OverrideTabListNameMixin {
    @Unique
    TabListSortInitializer module = ModuleManager.getInitializer(TabListSortInitializer.class);

    @Unique
    private static Map<String, Text> realPlayerGetDisplayNameSave = new HashMap<>();

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    /*
      note that: encoded player and real player will all call this function.
     */
    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    Text modifyPlayerListName(Text original) {
        MinecraftServer server = Fuji.SERVER;
        String name = player.getGameProfile().getName();

        /*
         1. only try to modify the encoded-player 's display name. since if tab_list.sort module is enabled,
         then the real player's display name will not be show in the tab list.

         2. for encoded-player, the getPlayerListName() call will always pass `original = null`

         */
        if (name.contains(TabListSortInitializer.META_SEPARATOR)) {
            name = TabListSortInitializer.decodeName(name);

            Text realPlayerGetDisplayName = realPlayerGetDisplayNameSave.get(name);
            // if nobody sets the display name, then we can set it.
            if (realPlayerGetDisplayName == null) {
                ServerPlayerEntity realPlayer = Fuji.SERVER.getPlayerManager().getPlayer(name);
                return ofText(realPlayer, false, Configs.configHandler.model().modules.tab_list.style.body);
            } else {
                // someone else set teh display name, we should respect it.
                return realPlayerGetDisplayName;
            }

        } else {
            // listen to real player's get display name invoke, and sync it to the encoded-player
            realPlayerGetDisplayNameSave.put(name, original);
            ServerPlayerEntity encodedPlayer = TabListSortInitializer.makeServerPlayerEntity(server, player);
            server.getPlayerManager().sendToAll(TabListSortInitializer.entryFromEncodedPlayer(List.of(encodedPlayer)));
        }

        return original;
    }
}
