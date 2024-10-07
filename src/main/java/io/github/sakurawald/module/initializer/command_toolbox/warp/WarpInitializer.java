package io.github.sakurawald.module.initializer.command_toolbox.warp;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.exception.AbortCommandExecutionException;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.core.structure.SpatialPose;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.wrapper.WarpName;
import io.github.sakurawald.module.initializer.command_toolbox.warp.config.model.WarpDataModel;
import io.github.sakurawald.module.initializer.command_toolbox.warp.structure.WarpEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

@CommandNode("warp")
public class WarpInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<WarpDataModel> data = new ObjectConfigurationHandler<>("warp.json", WarpDataModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("warp.json"), WarpInitializer.class));

    private static void ensureWarpExists(ServerPlayerEntity player, WarpName warpName) {
        String name = warpName.getValue();
        if (!data.model().name2warp.containsKey(name)) {
            LocaleHelper.sendMessageByKey(player, "warp.not_found", name);
            throw new AbortCommandExecutionException();
        }
    }

    @CommandNode("tp")
    private static int $tp(@CommandSource ServerPlayerEntity player, WarpName warpName) {
        ensureWarpExists(player, warpName);

        String name = warpName.getValue();
        WarpEntry entry = data.model().name2warp.get(name);
        entry.getPosition().teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("unset")
    @CommandRequirement(level = 4)
    private static int $unset(@CommandSource ServerPlayerEntity player, WarpName warpName) {
        ensureWarpExists(player, warpName);

        String name = warpName.getValue();
        data.model().name2warp.remove(name);
        LocaleHelper.sendMessageByKey(player, "warp.unset.success", name);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("set")
    @CommandRequirement(level = 4)
    private static int $set(@CommandSource ServerPlayerEntity player, WarpName warpName, Optional<Boolean> override) {
        String name = warpName.getValue();

        if (data.model().name2warp.containsKey(name)) {
            if (!override.orElse(false)) {
                LocaleHelper.sendMessageByKey(player, "warp.set.fail.need_override", name);
                return CommandHelper.Return.FAIL;
            }
        }

        data.model().name2warp.put(name, new WarpEntry(SpatialPose.of(player)));
        LocaleHelper.sendMessageByKey(player, "warp.set.success", name);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("list")
    private static int $list(@CommandSource ServerPlayerEntity player) {
        LocaleHelper.sendMessageByKey(player, "warp.list", data.model().name2warp.keySet());
        return CommandHelper.Return.SUCCESS;
    }

    @Override
    public void onInitialize() {
        data.scheduleWriteStorageJob(ScheduleManager.CRON_EVERY_MINUTE);
    }
}
