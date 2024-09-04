package io.github.sakurawald.module.initializer.command_toolbox.warp;

import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.command_toolbox.warp.config.model.WarpModel;
import io.github.sakurawald.module.common.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.wrapper.WarpName;
import io.github.sakurawald.module.initializer.command_toolbox.warp.structure.WarpEntry;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

@SuppressWarnings("LombokGetterMayBeUsed")
@CommandNode("warp")
public class WarpInitializer extends ModuleInitializer {

    @Getter
    private final ConfigHandler<WarpModel> data = new ObjectConfigHandler<>("warp.json", WarpModel.class);

    @Override
    public void onInitialize() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
    }

    @CommandNode("tp")
    private int $tp(@CommandSource ServerPlayerEntity player, WarpName warpName) {
        String name = warpName.getName();

        if (!data.model().warps.containsKey(name)) {
            MessageHelper.sendMessage(player, "warp.no_found", name);
            return 0;
        }

        WarpEntry entry = data.model().warps.get(name);
        entry.getPosition().teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("unset")
    @CommandRequirement(level = 4)
    private int $unset(@CommandSource ServerPlayerEntity player, WarpName warpName) {
        String name = warpName.getName();

        if (!data.model().warps.containsKey(name)) {
            MessageHelper.sendMessage(player, "warp.no_found", name);
            return 0;
        }

        data.model().warps.remove(name);
        MessageHelper.sendMessage(player, "warp.unset.success", name);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("set")
    @CommandRequirement(level = 4)
    private int $set(@CommandSource ServerPlayerEntity player, WarpName warpName, Optional<Boolean> override) {
        String name = warpName.getName();

        if (data.model().warps.containsKey(name)) {
            if (!override.orElse(false)) {
                MessageHelper.sendMessage(player, "warp.set.fail.need_override", name);
                return CommandHelper.Return.FAIL;
            }
        }

        data.model().warps.put(name, new WarpEntry(Position.of(player)));
        MessageHelper.sendMessage(player, "warp.set.success", name);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("list")
    private int $list(@CommandSource ServerPlayerEntity player) {
        MessageHelper.sendMessage(player, "warp.list", data.model().warps.keySet());
        return CommandHelper.Return.SUCCESS;
    }
}
