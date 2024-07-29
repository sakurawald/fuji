package io.github.sakurawald.module.initializer.cleaner;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.structure.TypeFormatter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;

public class CleanerInitializer extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("cleaner").requires(ctx -> ctx.hasPermissionLevel(4))
                .then(literal("clean").executes(this::clean)));
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> updateJobs());
    }

    @Override
    public void onReload() {
        updateJobs();
    }

    public void updateJobs() {
        Managers.getScheduleManager().cancelJobs(CleanerJob.class.getName());
        Managers.getScheduleManager().scheduleJob(CleanerJob.class, Configs.configHandler.model().modules.cleaner.cron);
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean ignoreEntity(Entity entity) {
        if (entity.getType().equals(EntityType.PLAYER)) return true;
        if (entity instanceof BlockAttachedEntity) return true;
        if (entity instanceof VehicleEntity) return true;

        var config = Configs.configHandler.model().modules.cleaner.ignore;

        if (config.ignoreItemEntity && entity instanceof ItemEntity) return true;
        if (config.ignoreLivingEntity && entity.isLiving()) return true;
        if (config.ignoreNamedEntity) {
            if (entity.hasCustomName()) return true;
            if (entity instanceof ItemEntity ie && ie.getStack().get(DataComponentTypes.CUSTOM_NAME) != null)
                return true;
        }
        if (config.ignoreEntityWithVehicle && entity.hasVehicle()) return true;
        if (config.ignoreEntityWithPassengers && entity.hasPassengers()) return true;
        if (config.ignoreGlowingEntity && entity.isGlowing()) return true;
        if (config.ignoreLeashedEntity && entity instanceof Leashable leashable && leashable.isLeashed()) return true;

        return false;
    }

    private int clean(CommandContext<ServerCommandSource> ctx) {
        clean();
        return CommandHelper.Return.SUCCESS;
    }

    private boolean shouldRemove(String key, int age) {
        Map<String, Integer> regex2age = Configs.configHandler.model().modules.cleaner.key2age;
        return regex2age.containsKey(key) && age >= regex2age.get(key);
    }

    private void clean() {
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
    }

    private void sendCleanerBroadcast(Map<String, Integer> counter) {
        // avoid spam
        if (counter.isEmpty()) return;

        LogUtil.info("[Cleaner] remove entities: {}",counter);

        Component hoverTextComponent = Component.text()
                .color(NamedTextColor.GOLD)
                .append(TypeFormatter.formatTypes(null, counter)).build();

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            Component component = MessageHelper.ofComponent(player, "cleaner.broadcast", counter.values().stream().mapToInt(Integer::intValue).sum());
            component = component.hoverEvent(HoverEvent.showText(hoverTextComponent));
            player.sendMessage(component);
        }
    }

    public static class CleanerJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            CleanerInitializer initializer = Managers.getModuleManager().getInitializer(CleanerInitializer.class);
            initializer.clean();
        }
    }

}
