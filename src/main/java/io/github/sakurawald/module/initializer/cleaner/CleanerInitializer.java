package io.github.sakurawald.module.initializer.cleaner;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.structure.TypeFormatter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.cleaner.config.model.CleanerModel;
import io.github.sakurawald.module.initializer.cleaner.job.CleanerJob;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@CommandNode("cleaner")
@CommandRequirement(level = 4)
public class CleanerInitializer extends ModuleInitializer {

    public ObjectConfigurationHandler<CleanerModel> config = new ObjectConfigurationHandler<>("config.cleaner.json", CleanerModel.class);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new CleanerJob().schedule());
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean ignoreEntity(Entity entity) {
        if (entity.getType().equals(EntityType.PLAYER)) return true;
        if (entity instanceof BlockAttachedEntity) return true;
        if (entity instanceof VehicleEntity) return true;

        var config = this.config.getModel().ignore;

        if (config.ignore_item_entity && entity instanceof ItemEntity) return true;
        if (config.ignore_living_entity && entity.isLiving()) return true;
        if (config.ignore_named_entity) {
            if (entity.hasCustomName()) return true;
            if (entity instanceof ItemEntity ie && ie.getStack().get(DataComponentTypes.CUSTOM_NAME) != null)
                return true;
        }
        if (config.ignore_entity_with_vehicle && entity.hasVehicle()) return true;
        if (config.ignore_entity_with_passengers && entity.hasPassengers()) return true;
        if (config.ignore_glowing_entity && entity.isGlowing()) return true;
        if (config.ignore_leashed_entity && entity instanceof Leashable leashable && leashable.isLeashed()) return true;

        return false;
    }

    private boolean shouldRemove(String key, int age) {
        Map<String, Integer> regex2age = config.getModel().key2age;
        return regex2age.containsKey(key) && age >= regex2age.get(key);
    }

    @CommandNode("clean")
    public int clean() {
        CompletableFuture.runAsync(() -> {
            Map<String, Integer> counter = new HashMap<>();

            for (ServerWorld world : ServerHelper.getDefaultServer().getWorlds()) {
                for (Entity entity : world.iterateEntities()) {
                    if (ignoreEntity(entity)) continue;

                    String key;
                    if (entity instanceof ItemEntity itemEntity) {
                        key = itemEntity.getStack().getTranslationKey();
                    } else {
                        key = entity.getType().getTranslationKey();
                    }

                    if (shouldRemove(key, entity.age)) {
                        counter.put(key, counter.getOrDefault(key, 0) + 1);
                        entity.discard();
                    }
                }
            }

            // output
            sendCleanerBroadcast(counter);
        });

        return CommandHelper.Return.SUCCESS;
    }

    private void sendCleanerBroadcast(Map<String, Integer> counter) {
        // avoid spam
        if (counter.isEmpty()) return;

        LogUtil.info("[cleaner] remove entities: {}",counter);

        Component hoverTextComponent = Component.text()
                .color(NamedTextColor.GOLD)
                .append(TypeFormatter.formatTypes(null, counter)).build();

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            Component component = LocaleHelper.getTextByKey(player, "cleaner.broadcast", counter.values().stream().mapToInt(Integer::intValue).sum()).asComponent();
            component = component.hoverEvent(HoverEvent.showText(hoverTextComponent));
            player.sendMessage(component);
        }
    }

}
