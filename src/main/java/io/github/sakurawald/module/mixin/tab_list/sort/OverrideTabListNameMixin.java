package io.github.sakurawald.module.mixin.tab_list.sort;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.sakurawald.core.auxiliary.RandomUtil;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import io.github.sakurawald.module.initializer.tab_list.sort.structure.TabListEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


// the last to check the return value
@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 125)
public abstract class OverrideTabListNameMixin {

    @Unique
    final private static @NotNull Map<String, Text> realPlayerGetDisplayNameSave = new HashMap<>();

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    /*
      note that: dummy player and real player will all call this function.
     */
    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    Text modifyPlayerListName(Text original) {
        MinecraftServer server = ServerHelper.getDefaultServer();
        String name = player.getGameProfile().getName();

        /*
         1. only try to modify the dummy-player 's display name. since if tab_list.sort module is enabled,
         then the real player's display name will not be show in the tab list.

         2. for dummy-player, the getPlayerListName() call will always pass `original = null`

         */
        if (TabListEntry.isDummyPlayer(player)) {
            Optional<TabListEntry> optional = TabListEntry.getEntryFromDummyPlayer(player);
            if (optional.isEmpty()) return original;

            TabListEntry entry = optional.get();
            name = entry.getRealPlayer().getGameProfile().getName();

            Text realPlayerGetDisplayName = realPlayerGetDisplayNameSave.get(name);
            // if nobody sets the display name, then we can set it.
            // someone else set teh display name, we should respect it.
            return Objects.requireNonNullElseGet(realPlayerGetDisplayName, () -> LocaleHelper.getTextByValue(entry.getRealPlayer(), RandomUtil.drawList(TabListInitializer.config.getModel().style.body)));

        } else {
            /* listen to real player's get display name invoke, and sync it to the dummy-player */

            // cache result, if the query response is the same.
            if (realPlayerGetDisplayNameSave.get(name) == original) return original;

            realPlayerGetDisplayNameSave.put(name, original);
            ServerPlayerEntity dummyPlayer = TabListEntry.getEntryFromRealPlayer(player).getDummyPlayer();

            server.getPlayerManager().sendToAll(TabListEntry.makePacket(List.of(dummyPlayer)));
        }

        return original;
    }
}
