package io.github.sakurawald.module.initializer.command_toolbox.warp;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.command_toolbox.warp.model.WarpModel;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.warp.adapter.WarpName;
import io.github.sakurawald.module.initializer.command_toolbox.warp.structure.WarpEntry;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.naming.OperationNotSupportedException;
import java.util.Optional;

@SuppressWarnings("LombokGetterMayBeUsed")
@Command("warp")
public class WarpInitializer extends ModuleInitializer {

    @Getter
    private final ConfigHandler<WarpModel> data = new ObjectConfigHandler<>("warp.json", WarpModel.class);

    @Override
    public void onInitialize() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() throws OperationNotSupportedException {
        data.loadFromDisk();
    }

    @Command("tp")
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

    @Command("unset")
    @CommandPermission(level = 4)
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

    @Command("set")
    @CommandPermission(level = 4)
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

    @Command("list")
    private int $list(@CommandSource ServerPlayerEntity player) {
        MessageHelper.sendMessage(player, "warp.list", data.model().warps.keySet());
        return CommandHelper.Return.SUCCESS;
    }
}
