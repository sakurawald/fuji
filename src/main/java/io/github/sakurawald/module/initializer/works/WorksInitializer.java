package io.github.sakurawald.module.initializer.works;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.WorksModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.gui.InputSignGui;
import io.github.sakurawald.module.initializer.works.work_type.NonProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.ProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("SameReturnValue")

public class WorksInitializer extends ModuleInitializer {

    public static final ConfigHandler<WorksModel> worksHandler = new ObjectConfigHandler<>("works.json", WorksModel.class);
    private final int PAGE_SIZE = 9 * 5;


    @Override
    public void onInitialize() {
        worksHandler.loadFromDisk();
        ServerLifecycleEvents.SERVER_STARTED.register(this::registerScheduleTask);
    }

    @Override
    public void onReload() {
        worksHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    public void registerScheduleTask(MinecraftServer server) {
        ScheduleUtil.addJob(WorksScheduleJob.class, null, null, ScheduleUtil.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
            }
        });
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("works").executes(this::$works));
    }

    private void $addWork(ServerPlayerEntity player) {
        new InputSignGui(player, MessageUtil.getString(player, "works.work.add.prompt.input.name")) {
            @Override
            public void onClose() {
                /* input name */
                String name = this.getLine(0).getString().trim();
                if (name.isBlank()) {
                    MessageUtil.sendActionBar(player, "works.work.add.empty_name");
                    return;
                }

                /* input type */
                SimpleGui selectWorkTypeGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
                selectWorkTypeGui.setLockPlayerInventory(true);
                selectWorkTypeGui.setTitle(MessageUtil.ofVomponent(player, "works.work.add.select_work_type.title"));
                for (int i = 0; i < 27; i++) {
                    selectWorkTypeGui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
                }
                selectWorkTypeGui.setSlot(11, new GuiElementBuilder().setItem(Items.GUNPOWDER).setName(MessageUtil.ofVomponent(player, "works.non_production_work.name")).setCallback(() -> {
                    // add
                    worksHandler.model().works.add(0, new NonProductionWork(player, name));
                    MessageUtil.sendActionBar(player, "works.work.add.done");
                    MessageUtil.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
                    selectWorkTypeGui.close();
                }));
                selectWorkTypeGui.setSlot(15, new GuiElementBuilder().setItem(Items.REDSTONE).setName(MessageUtil.ofVomponent(player, "works.production_work.name")).setCallback(() -> {
                    // add
                    ProductionWork work = new ProductionWork(player, name);
                    worksHandler.model().works.add(0, work);
                    MessageUtil.sendActionBar(player, "works.work.add.done");
                    MessageUtil.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
                    selectWorkTypeGui.close();

                    // input sample distance
                    work.openInputSampleDistanceGui(player);
                }));
                selectWorkTypeGui.open();

            }
        }.open();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasPermission(ServerPlayerEntity player, Work work) {
        return player.getGameProfile().getName().equals(work.creator) || player.hasPermissionLevel(4);
    }

    private void $listWorks(ServerPlayerEntity player, @Nullable List<Work> source, int page) {
        if (source == null) {
            source = worksHandler.model().works;
        }

        final SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(MessageUtil.ofVomponent(player, "works.list.title", page + 1));

        /* draw content */
        for (int slotIndex = 0; slotIndex < PAGE_SIZE; slotIndex++) {
            int workIndex = PAGE_SIZE * page + slotIndex;
            if (workIndex < 0 || workIndex >= source.size()) break;
            Work work = source.get(workIndex);
            gui.setSlot(slotIndex, new GuiElementBuilder()
                    .setItem(work.asItem())
                    .setName(MessageUtil.ofVomponent(work.name))
                    .setLore(work.asLore(player))
                    .setCallback((index, clickType, actionType) -> {
                        /* left click -> visit */
                        if (clickType.isLeft) {
                            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(work.level));
                            ServerWorld level = Fuji.SERVER.getWorld(worldKey);
                            //noinspection DataFlowIssue
                            player.teleport(level, work.x, work.y, work.z, work.yaw, work.pitch);
                            gui.close();
                            return;
                        }
                        /* shift + right click -> specialized settings */
                        if (clickType.isRight && clickType.shift) {
                            if (!hasPermission(player, work)) {
                                MessageUtil.sendActionBar(player, "works.work.set.no_perm");
                                return;
                            }
                            work.openSpecializedSettingsGui(player, gui);
                            gui.close();
                            return;
                        }
                        /* right click -> general settings */
                        if (clickType.isRight) {
                            // check permission
                            if (!hasPermission(player, work)) {
                                MessageUtil.sendActionBar(player, "works.work.set.no_perm");
                                return;
                            }
                            work.openGeneralSettingsGui(player, gui);
                            gui.close();
                        }
                    }));
        }

        /* draw navigator */
        for (int i = 45; i < 54; i++) {
            gui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
        }
        List<Work> finalSource = source;
        gui.setSlot(45, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofVomponent(player, "previous_page"))
                .setSkullOwner(GuiUtil.PREVIOUS_PAGE_ICON)
                .setCallback(() -> {
                    if (page == 0) return;
                    $listWorks(player, finalSource, page - 1);
                }));
        gui.setSlot(48, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofVomponent(player, "works.list.add"))
                .setSkullOwner(GuiUtil.PLUS_ICON)
                .setCallback(() -> $addWork(player))
        );
        if (source == worksHandler.model().works) {
            gui.setSlot(49, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(MessageUtil.ofVomponent(player, "works.list.my_works"))
                    .setSkullOwner(GuiUtil.HEART_ICON)
                    .setCallback(() -> $myWorks(player))
            );
        } else {
            gui.setSlot(49, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(MessageUtil.ofVomponent(player, "works.list.all_works"))
                    .setSkullOwner(GuiUtil.A_ICON)
                    .setCallback(() -> $listWorks(player, null, 0))
            );
        }
        gui.setSlot(50, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofVomponent(player, "works.list.help"))
                .setSkullOwner(GuiUtil.QUESTION_MARK_ICON)
                .setLore(MessageUtil.ofVomponents(player, "works.list.help.lore")));
        gui.setSlot(52, new GuiElementBuilder()
                .setItem(Items.COMPASS)
                .setName(MessageUtil.ofVomponent(player, "search"))
                .setCallback(() -> $searchWorks(player, finalSource))
        );
        gui.setSlot(53, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofVomponent(player, "next_page"))
                .setSkullOwner(GuiUtil.NEXT_PAGE_ICON)
                .setCallback(() -> {
                    if ((page + 1) * PAGE_SIZE >= finalSource.size()) return;
                    $listWorks(player, finalSource, page + 1);
                })
        );
        gui.open();
    }

    private void $searchWorks(ServerPlayerEntity player, List<Work> source) {
        /* input keywords */
        new InputSignGui(player, null) {
            @Override
            public void onClose() {
                List<Work> filterWorks = null;
                String key = combineAllLinesReturnNull();
                if (key != null) {
                    filterWorks = source.stream().filter(w ->
                            w.creator.contains(key)
                                    || w.name.contains(key)
                                    || (w.introduction != null && w.introduction.contains(key))
                                    || w.level.contains(key)
                                    || w.getIcon().contains(key)
                                    || (w instanceof ProductionWork pw && pw.sample.sampleCounter != null && pw.sample.sampleCounter.keySet().stream().anyMatch(k -> k.contains(key)))
                    ).toList();
                }
                $listWorks(player, filterWorks, 0);
            }
        }.open();

    }

    private void $myWorks(ServerPlayerEntity player) {
        List<Work> works = worksHandler.model().works;
        List<Work> myWorks = works.stream().filter(w -> w.creator.equals(player.getGameProfile().getName())).toList();
        $listWorks(player, myWorks, 0);
    }

    private int $works(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            $listWorks(player, null, 0);
            return Command.SINGLE_SUCCESS;
        });
    }

    public static class WorksScheduleJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            // save current works data
            MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
            if (server.isRunning()) {
                worksHandler.saveToDisk();
            }

            // run schedule method
            HashSet<Work> works = new HashSet<>();
            WorksCache.getBlockpos2works().values().forEach(works::addAll);
            WorksCache.getEntity2works().values().forEach(works::addAll);
            works.forEach(work -> {
                if (work instanceof ScheduleMethod sm) sm.onSchedule();
            });
        }
    }
}

