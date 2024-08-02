package io.github.sakurawald.module.initializer.head;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.head.model.HeadModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.head.gui.HeadGui;
import io.github.sakurawald.module.initializer.head.privoder.HeadProvider;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

// Special thanks to: https://modrinth.com/mod/headindex
public class HeadInitializer extends ModuleInitializer {

    public static final ConfigHandler<HeadModel> headHandler = new ObjectConfigHandler<>("head.json", HeadModel.class);

    @Override
    public void onInitialize() {
        headHandler.loadFromDisk();
    }

    @Override
    public void onReload() {
        headHandler.loadFromDisk();
    }

    @Command("head sync")
    @CommandPermission(level = 4)
    private int $sync(@CommandSource CommandContext<ServerCommandSource> ctx) {
        HeadProvider.fetchData();
        return CommandHelper.Return.SUCCESS;
    }

    @Command("head")
    public int $head(@CommandSource ServerPlayerEntity player) {
        new HeadGui(player).open();
        return CommandHelper.Return.SUCCESS;
    }

    @Command("head gui")
    public int $gui(@CommandSource ServerPlayerEntity player) {
        return $head(player);
    }
}
