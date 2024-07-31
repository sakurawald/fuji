package io.github.sakurawald.module.initializer.home;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.HomeModel;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.home.adapter.HomeName;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("LombokGetterMayBeUsed")
public class HomeInitializer extends ModuleInitializer {

    @Getter
    private final ConfigHandler<HomeModel> data = new ObjectConfigHandler<>("home.json", HomeModel.class);

    public void onInitialize() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    public Map<String, Position> ofHomes(@NotNull ServerPlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        Map<String, Map<String, Position>> homes = data.model().homes;
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        return homes.get(playerName);
    }

    @Command("home tp")
    private int $tp(@CommandSource ServerPlayerEntity player, HomeName home) {
        Map<String, Position> name2position = ofHomes(player);
        String homeName = home.getString();
        if (!name2position.containsKey(homeName)) {
            MessageHelper.sendMessage(player, "home.no_found", homeName);
            return 0;
        }

        Position position = name2position.get(homeName);
        position.teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    @Command("home unset")
    private int $unset(@CommandSource ServerPlayerEntity player, HomeName home) {
        Map<String, Position> name2position = ofHomes(player);
        String homeName = home.getString();
        if (!name2position.containsKey(homeName)) {
            MessageHelper.sendMessage(player, "home.no_found", homeName);
            return 0;
        }

        name2position.remove(homeName);
        MessageHelper.sendMessage(player, "home.unset.success", homeName);
        return CommandHelper.Return.SUCCESS;
    }

    @Command("home set")
    private int $set(@CommandSource ServerPlayerEntity player, HomeName home, Optional<Boolean> override) {
        Map<String, Position> name2position = ofHomes(player);
        String homeName = home.getString();

        if (name2position.containsKey(homeName)) {
            if (override.orElse(false)) {
                MessageHelper.sendMessage(player, "home.set.fail.need_override", homeName);
                return CommandHelper.Return.FAIL;
            }
        }

        Optional<Integer> limit = PermissionHelper.getMeta(player, "fuji.home.home_limit", Integer::valueOf);
        if (limit.isPresent() && name2position.size() >= limit.get()) {
            MessageHelper.sendMessage(player, "home.set.fail.limit");
            return CommandHelper.Return.FAIL;
        }

        name2position.put(homeName, Position.of(player));
        MessageHelper.sendMessage(player, "home.set.success", homeName);
        return CommandHelper.Return.SUCCESS;
    }


    @Command("home list")
    private int $list(@CommandSource ServerPlayerEntity player) {
        MessageHelper.sendMessage(player, "home.list", ofHomes(player).keySet());
        return CommandHelper.Return.SUCCESS;
    }

}
