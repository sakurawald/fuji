package fun.sakurawald.module.works;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fun.sakurawald.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@SuppressWarnings("SameReturnValue")
@Slf4j
public class WorksModule {

    private static final int PAGE_SIZE = 9 * 5;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public static void registerScheduleTask(MinecraftServer server) {
        // async task
        executorService.scheduleAtFixedRate(() -> {

            // save current works data
            ConfigManager.worksWrapper.saveToDisk();

            // check works sampling


        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("works")
                        .then(Commands.literal("add").then(argument("name", StringArgumentType.greedyString()).executes(WorksModule::$add)))
                        .then(Commands.literal("remove").then(argument("uuid", StringArgumentType.word()).executes(WorksModule::$remove)))
                        .then(Commands.literal("list").then(argument("page", IntegerArgumentType.integer(0)).executes(WorksModule::$list)).executes(WorksModule::$list))
                        .then(Commands.literal("set")
                                .then(argument("uuid", StringArgumentType.word())
                                        .then(literal("name").then(argument("name", StringArgumentType.greedyString()).executes(WorksModule::$set)))
                                        .then(literal("introduction").then(argument("introduction", StringArgumentType.greedyString()).executes(WorksModule::$set)))
                                        .then(literal("residence").then(argument("residence", StringArgumentType.word()).executes(WorksModule::$set)))
                                        .then(literal("icon").executes(WorksModule::$set)))
                        )
        );
    }

    private static int $set(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        String uuid = StringArgumentType.getString(ctx, "uuid");
        String key = null;
        String value;
        if (ctx.getNodes().size() == 5) {
            key = ctx.getNodes().get(3).getNode().getName();
            value = StringArgumentType.getString(ctx, key);
        } else {
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.isEmpty()) {
                player.sendMessage(Component.text("You must hold an item in your hand."));
                return Command.SINGLE_SUCCESS;
            }

            value = BuiltInRegistries.ITEM.getKey(mainHandItem.getItem()).toString();
        }

        log.warn("key = {}, value = {}", key, value);

        Work work = ConfigManager.worksWrapper.instance().works.stream().findFirst().filter(w -> w.id.equals(uuid)).orElse(null);
        if (work == null) {
            player.sendMessage(Component.text("Work not found."));
            return Command.SINGLE_SUCCESS;
        }

        // check permission
        if (!work.getCreator().equals(player.getGameProfile().getName())) {
            player.sendMessage(Component.text("You don't have permission to remove this work."));
            return Command.SINGLE_SUCCESS;
        }

        // set
        if (key != null) {
            switch (key) {
                case "name":
                    work.name = value;
                case "introduction":
                    work.introduction = value;
                case "residence":
                    work.residence = value;
            }
        } else {
            work.icon = value;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int $list(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        int page;
        try {
            page = IntegerArgumentType.getInteger(ctx, "page");
        } catch (Exception e) {
            page = 0;
        }
        ArrayList<Work> works = ConfigManager.worksWrapper.instance().works;

        SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x6, player, false);
        gui.setTitle(net.minecraft.network.chat.Component.literal("Works Collection"));
        gui.setLockPlayerInventory(true);

        for (int slotIndex = 0; slotIndex < PAGE_SIZE; slotIndex++) {
            int workIndex = PAGE_SIZE * page + slotIndex;
            if (workIndex >= works.size()) break;
            log.warn("set slotIndex = {}", slotIndex);
            gui.setSlot(slotIndex, works.get(workIndex).asItemStack());
            gui.setSlot(slotIndex, new GuiElementBuilder()
                    .setItem());
        }

        gui.open();
        return Command.SINGLE_SUCCESS;
    }

    private static int $remove(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getPlayer() == null) return Command.SINGLE_SUCCESS;

        String uuid = StringArgumentType.getString(ctx, "uuid");
        Work work = ConfigManager.worksWrapper.instance().works.stream().findFirst().filter(w -> w.id.equals(uuid)).orElse(null);

        if (work == null) {
            ctx.getSource().getPlayer().sendMessage(Component.text("Work not found."));
            return Command.SINGLE_SUCCESS;
        }
        // check permission
        if (!work.getCreator().equals(ctx.getSource().getPlayer().getGameProfile().getName())) {
            ctx.getSource().getPlayer().sendMessage(Component.text("You don't have permission to remove this work."));
            return Command.SINGLE_SUCCESS;
        }

        // remove
        ConfigManager.worksWrapper.instance().works.remove(uuid);

        return Command.SINGLE_SUCCESS;
    }


    private static int $add(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        String creator = player.getGameProfile().getName();
        String name = StringArgumentType.getString(ctx, "name");
        ConfigManager.worksWrapper.instance().works.add(new Work(creator, name));
        return Command.SINGLE_SUCCESS;
    }

}

