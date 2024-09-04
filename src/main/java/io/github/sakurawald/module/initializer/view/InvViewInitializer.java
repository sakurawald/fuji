package io.github.sakurawald.module.initializer.view;

import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.OfflinePlayerName;
import io.github.sakurawald.module.common.exception.SnackException;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.view.gui.EnderChestRedirectScreen;
import io.github.sakurawald.module.initializer.view.gui.InventoryRedirectScreen;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandNode("view")
@CommandRequirement(level = 4)
public class InvViewInitializer extends ModuleInitializer {

    void checkSelfView(ServerPlayerEntity source, OfflinePlayerName target) {
        if (source.getGameProfile().getName().equals(target.getString())) {
            MessageHelper.sendMessage(source, "view.failed.self_view");
            throw new SnackException();
        }
    }

    @CommandNode("inv")
    int inv(@CommandSource ServerPlayerEntity source, OfflinePlayerName target) {
        checkSelfView(source, target);

        source.openHandledScreen(new InventoryRedirectScreen(source, target.getString()).makeFactory());
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("ender")
    int ender(@CommandSource ServerPlayerEntity source, OfflinePlayerName target) {
        checkSelfView(source, target);

        source.openHandledScreen(new EnderChestRedirectScreen(source, target.getString()).makeFactory());
        return CommandHelper.Return.SUCCESS;
    }
}
