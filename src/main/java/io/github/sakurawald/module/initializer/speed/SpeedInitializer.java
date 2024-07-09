package io.github.sakurawald.module.initializer.speed;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LuckPermsUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.luckperms.api.event.player.PlayerLoginProcessEvent;
import net.luckperms.api.event.user.UserCacheLoadEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

@Slf4j
public class SpeedInitializer extends ModuleInitializer {
    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register(e -> {
            // add event post user login
            ...

            // will also be trigger if the player disconnect
            LuckPermsUtil.getAPI().getEventBus().subscribe(UserDataRecalculateEvent.class, (event) -> {
                log.warn("luckperms event {} -> player {}", event.getClass().getSimpleName(), event.getUser().getUsername());
                act(event.getUser().getUsername());
            });

        });
    }

    public void act(String player) {


        Optional<? extends Float> metaValue = LuckPermsUtil.getMetaValue(player, "fuji.flyspeed", Float::valueOf);
        Float v = metaValue.get();
        log.warn("player {} data changed. set speed to {}", player, v);
        ServerPlayerEntity playerEntity = Fuji.SERVER.getPlayerManager().getPlayer(player);

        log.warn("current playerEntity = {}", playerEntity);

//        playerEntity.getAbilities().setWalkSpeed(v);
//        playerEntity.sendAbilitiesUpdate();
    }

}
