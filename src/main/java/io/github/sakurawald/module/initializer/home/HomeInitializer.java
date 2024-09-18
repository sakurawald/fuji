package io.github.sakurawald.module.initializer.home;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.core.structure.SpatialPose;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.home.command.argument.wrapper.HomeName;
import io.github.sakurawald.module.initializer.home.config.model.HomeModel;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("LombokGetterMayBeUsed")
public class HomeInitializer extends ModuleInitializer {

    @Getter
    private final BaseConfigurationHandler<HomeModel> data = new ObjectConfigurationHandler<>("home.json", HomeModel.class);

    public void onInitialize() {
        data.readStorage();
        data.scheduleSaveConfigurationHandlerJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() {
        data.readStorage();
    }

    public Map<String, SpatialPose> ofHomes(@NotNull ServerPlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        Map<String, Map<String, SpatialPose>> homes = data.getModel().homes;
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        return homes.get(playerName);
    }

    @CommandNode("home tp")
    private int $tp(@CommandSource ServerPlayerEntity player, HomeName home) {
        Map<String, SpatialPose> name2position = ofHomes(player);
        String homeName = home.getValue();
        if (!name2position.containsKey(homeName)) {
            LocaleHelper.sendMessageByKey(player, "home.not_found", homeName);
            return 0;
        }

        SpatialPose spatialPose = name2position.get(homeName);
        spatialPose.teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("home unset")
    private int $unset(@CommandSource ServerPlayerEntity player, HomeName home) {
        Map<String, SpatialPose> name2position = ofHomes(player);
        String homeName = home.getValue();
        if (!name2position.containsKey(homeName)) {
            LocaleHelper.sendMessageByKey(player, "home.not_found", homeName);
            return 0;
        }

        name2position.remove(homeName);
        LocaleHelper.sendMessageByKey(player, "home.unset.success", homeName);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("home set")
    private int $set(@CommandSource ServerPlayerEntity player, HomeName home, Optional<Boolean> override) {
        Map<String, SpatialPose> name2position = ofHomes(player);
        String homeName = home.getValue();

        if (name2position.containsKey(homeName)) {
            if (!override.orElse(false)) {
                LocaleHelper.sendMessageByKey(player, "home.set.fail.need_override", homeName);
                return CommandHelper.Return.FAIL;
            }
        }

        Optional<Integer> limit = PermissionHelper.getMeta(player.getUuid(), "fuji.home.home_limit", Integer::valueOf);
        if (limit.isPresent() && name2position.size() >= limit.get()) {
            LocaleHelper.sendMessageByKey(player, "home.set.fail.limit");
            return CommandHelper.Return.FAIL;
        }

        name2position.put(homeName, SpatialPose.of(player));
        LocaleHelper.sendMessageByKey(player, "home.set.success", homeName);
        return CommandHelper.Return.SUCCESS;
    }


    @CommandNode("home list")
    private int $list(@CommandSource ServerPlayerEntity player) {
        LocaleHelper.sendMessageByKey(player, "home.list", ofHomes(player).keySet());
        return CommandHelper.Return.SUCCESS;
    }

}
