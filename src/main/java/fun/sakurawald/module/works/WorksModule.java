package fun.sakurawald.module.works;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fun.sakurawald.ServerMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.works.gui.InputSignGui;
import fun.sakurawald.module.works.work_type.NonProductionWork;
import fun.sakurawald.module.works.work_type.ProductionWork;
import fun.sakurawald.module.works.work_type.Work;
import fun.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static fun.sakurawald.util.MessageUtil.*;

@SuppressWarnings("SameReturnValue")
@Slf4j
public class WorksModule {

    public static final String PREVIOUS_PAGE_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==";
    public static final String PLUS_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=";
    public static final String HEART_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM2ZmViZWNhN2M0ODhhNjY3MWRjMDcxNjU1ZGRlMmExYjY1YzNjY2IyMGI2ZThlYWY5YmZiMDhlNjRiODAifX19";
    public static final String A_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=";
    public static final String QUESTION_MARK_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlYzg1YmM4MDYxYmRhM2UxZDQ5Zjc1NDQ2NDllNjVjODI3MmNhNTZmNzJkODM4Y2FmMmNjNDgxNmI2OSJ9fX0=";
    public static final String NEXT_PAGE_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0ZjY4YzhmYjI3OWU1MGFiNzg2ZjlmYTU0Yzg4Y2E0ZWNmZTFlYjVmZDVmMGMzOGM1NGM5YjFjNzIwM2Q3YSJ9fX0=";
    private static final int PAGE_SIZE = 9 * 5;

    @SuppressWarnings("unused")
    public static void registerScheduleTask(MinecraftServer server) {
        ServerMain.getScheduledExecutor().scheduleAtFixedRate(() -> {
            // save current works data
            if (ServerMain.SERVER.isRunning()) {
                ConfigManager.worksWrapper.saveToDisk();
            }

            // run schedule method
            BlockPosCache.getBlockpos2works().values().forEach(v -> v.forEach(w -> {
                if (w instanceof ScheduleMethod sm) sm.onSchedule();
            }));
        }, 20, 60, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unused")
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("works").executes(WorksModule::$works));
    }

    private static void $addWork(ServerPlayer player) {
        new InputSignGui(player, ofString(player, "works.work.add.prompt.input.name")) {
            @Override
            public void onClose() {
                /* input name */
                String name = this.getLine(0).getString().trim();
                if (name.isBlank()) {
                    MessageUtil.sendActionBar(player, "works.work.add.empty_name");
                    return;
                }

                /* input type */
                SimpleGui selectWorkTypeGui = new SimpleGui(MenuType.GENERIC_9x3, player, false);
                selectWorkTypeGui.setLockPlayerInventory(true);
                selectWorkTypeGui.setTitle(ofVomponent(player, "works.work.add.select_work_type.title"));
                for (int i = 0; i < 27; i++) {
                    selectWorkTypeGui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
                }
                selectWorkTypeGui.setSlot(11, new GuiElementBuilder().setItem(Items.GUNPOWDER).setName(ofVomponent(player, "works.non_production_work.name")).setCallback(() -> {
                    // add
                    ConfigManager.worksWrapper.instance().works.add(0, new NonProductionWork(player, name));
                    MessageUtil.sendActionBar(player, "works.work.add.done");
                    MessageUtil.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
                    selectWorkTypeGui.close();
                }));
                selectWorkTypeGui.setSlot(15, new GuiElementBuilder().setItem(Items.REDSTONE).setName(ofVomponent(player, "works.production_work.name")).setCallback(() -> {
                    // add
                    ProductionWork work = new ProductionWork(player, name);
                    ConfigManager.worksWrapper.instance().works.add(0, work);
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
    private static boolean hasPermission(ServerPlayer player, Work work) {
        return player.getGameProfile().getName().equals(work.creator) || player.hasPermissions(4);
    }

    private static void $listWorks(ServerPlayer player, List<Work> source, int page) {
        if (source == null) {
            source = ConfigManager.worksWrapper.instance().works;
        }

        final SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x6, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(ofVomponent(player, "works.list.title", page + 1));

        /* draw content */
        for (int slotIndex = 0; slotIndex < PAGE_SIZE; slotIndex++) {
            int workIndex = PAGE_SIZE * page + slotIndex;
            if (workIndex < 0 || workIndex >= source.size()) break;
            Work work = source.get(workIndex);
            gui.setSlot(slotIndex, new GuiElementBuilder()
                    .setItem(work.asItem())
                    .setName(ofVomponentFromMiniMessage(work.name))
                    .setLore(work.asLore(player))
                    .setCallback((index, clickType, actionType) -> {
                        /* left click -> visit */
                        if (clickType.isLeft) {
                            ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(work.level));
                            ServerLevel level = ServerMain.SERVER.getLevel(worldKey);
                            player.teleportTo(level, work.x, work.y, work.z, work.yaw, work.pitch);
                            gui.close();
                            return;
                        }
                        /* middle click -> specialized settings */
                        if (clickType.isMiddle) {
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
                .setName(ofVomponent(player, "previous_page"))
                .setSkullOwner(PREVIOUS_PAGE_ICON)
                .setCallback(() -> {
                    if (page == 0) return;
                    $listWorks(player, finalSource, page - 1);
                }));
        gui.setSlot(48, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(ofVomponent(player, "works.list.add"))
                .setSkullOwner(PLUS_ICON)
                .setCallback(() -> $addWork(player))
        );
        if (source == ConfigManager.worksWrapper.instance().works) {
            gui.setSlot(49, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(ofVomponent(player, "works.list.my_works"))
                    .setSkullOwner(HEART_ICON)
                    .setCallback(() -> $myWorks(player))
            );
        } else {
            gui.setSlot(49, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(ofVomponent(player, "works.list.all_works"))
                    .setSkullOwner(A_ICON)
                    .setCallback(() -> $listWorks(player, null, 0))
            );
        }
        gui.setSlot(50, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(ofVomponent(player, "works.list.help"))
                .setSkullOwner(QUESTION_MARK_ICON)
                .setLore(ofVomponents(player, "works.list.help.lore")));
        gui.setSlot(53, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(ofVomponent(player, "next_page"))
                .setSkullOwner(NEXT_PAGE_ICON)
                .setCallback(() -> {
                    if ((page + 1) * PAGE_SIZE >= finalSource.size()) return;
                    $listWorks(player, finalSource, page + 1);
                })
        );
        gui.open();
    }

    private static void $myWorks(ServerPlayer player) {
        List<Work> works = ConfigManager.worksWrapper.instance().works;
        List<Work> myWorks = works.stream().filter(w -> w.creator.equals(player.getGameProfile().getName())).toList();
        $listWorks(player, myWorks, 0);
    }

    private static int $works(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        $listWorks(player, null, 0);
        return Command.SINGLE_SUCCESS;
    }
}

