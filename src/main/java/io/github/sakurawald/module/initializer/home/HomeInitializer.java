package io.github.sakurawald.module.initializer.home;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigHandler;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.core.structure.Position;
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
    private final ConfigHandler<HomeModel> data = new ObjectConfigHandler<>("home.json", HomeModel.class);

    public void onInitialize() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
    }

    public Map<String, Position> ofHomes(@NotNull ServerPlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        Map<String, Map<String, Position>> homes = data.model().homes;
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        return homes.get(playerName);
    }

    @CommandNode("home tp")
    private int $tp(@CommandSource ServerPlayerEntity player, HomeName home) {
        Map<String, Position> name2position = ofHomes(player);
        String homeName = home.getValue();
        if (!name2position.containsKey(homeName)) {
            LanguageHelper.sendMessageByKey(player, "home.not_found", homeName);
            return 0;
        }

        Position position = name2position.get(homeName);
        position.teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("home unset")
    private int $unset(@CommandSource ServerPlayerEntity player, HomeName home) {
        Map<String, Position> name2position = ofHomes(player);
        String homeName = home.getValue();
        if (!name2position.containsKey(homeName)) {
            LanguageHelper.sendMessageByKey(player, "home.not_found", homeName);
            return 0;
        }

        name2position.remove(homeName);
        LanguageHelper.sendMessageByKey(player, "home.unset.success", homeName);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("home set")
    private int $set(@CommandSource ServerPlayerEntity player, HomeName home, Optional<Boolean> override) {
        Map<String, Position> name2position = ofHomes(player);
        String homeName = home.getValue();

        if (name2position.containsKey(homeName)) {
            if (!override.orElse(false)) {
                LanguageHelper.sendMessageByKey(player, "home.set.fail.need_override", homeName);
                return CommandHelper.Return.FAIL;
            }
        }

        Optional<Integer> limit = PermissionHelper.getMeta(player.getUuid(), "fuji.home.home_limit", Integer::valueOf);
        if (limit.isPresent() && name2position.size() >= limit.get()) {
            LanguageHelper.sendMessageByKey(player, "home.set.fail.limit");
            return CommandHelper.Return.FAIL;
        }

        name2position.put(homeName, Position.of(player));
        LanguageHelper.sendMessageByKey(player, "home.set.success", homeName);
        return CommandHelper.Return.SUCCESS;
    }


    @CommandNode("home list")
    private int $list(@CommandSource ServerPlayerEntity player) {
        LanguageHelper.sendMessageByKey(player, "home.list", ofHomes(player).keySet());
        return CommandHelper.Return.SUCCESS;
    }

}
