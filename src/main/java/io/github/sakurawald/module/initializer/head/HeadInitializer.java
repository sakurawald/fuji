package io.github.sakurawald.module.initializer.head;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.annotation.Cite;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.head.config.model.HeadConfigModel;
import io.github.sakurawald.module.initializer.head.gui.HeadGui;
import io.github.sakurawald.module.initializer.head.privoder.HeadProvider;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@Cite("https://github.com/PotatoPresident/HeadIndex")
public class HeadInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<HeadConfigModel> headHandler = new ObjectConfigurationHandler<>("head.json", HeadConfigModel.class).addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("head.json"),HeadInitializer.class));

    @CommandNode("head sync")
    @CommandRequirement(level = 4)
    private static int $sync(@CommandSource CommandContext<ServerCommandSource> ctx) {
        HeadProvider.fetchData();
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("head")
    private static int $head(@CommandSource ServerPlayerEntity player) {
        new HeadGui(player).open();
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("head gui")
    private static int $gui(@CommandSource ServerPlayerEntity player) {
        return $head(player);
    }
}
