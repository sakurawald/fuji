package io.github.sakurawald.module.initializer.view;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.OfflinePlayerName;
import io.github.sakurawald.core.command.exception.SnackException;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.view.gui.EnderChestRedirectScreen;
import io.github.sakurawald.module.initializer.view.gui.InventoryRedirectScreen;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandNode("view")
@CommandRequirement(level = 4)
public class InvViewInitializer extends ModuleInitializer {

    void checkSelfView(ServerPlayerEntity source, OfflinePlayerName target) {
        if (source.getGameProfile().getName().equals(target.getValue())) {
            LocaleHelper.sendMessageByKey(source, "view.failed.self_view");
            throw new SnackException();
        }
    }

    @CommandNode("inv")
    int inv(@CommandSource ServerPlayerEntity source, OfflinePlayerName target) {
        checkSelfView(source, target);

        source.openHandledScreen(new InventoryRedirectScreen(source, target.getValue()).makeFactory());
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("ender")
    int ender(@CommandSource ServerPlayerEntity source, OfflinePlayerName target) {
        checkSelfView(source, target);

        source.openHandledScreen(new EnderChestRedirectScreen(source, target.getValue()).makeFactory());
        return CommandHelper.Return.SUCCESS;
    }
}
