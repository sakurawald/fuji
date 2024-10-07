package io.github.sakurawald.module.initializer.pvp;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.pvp.config.model.PvPDataModel;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;


public class PvpInitializer extends ModuleInitializer {

    private static final BaseConfigurationHandler<PvPDataModel> pvpHandler = new ObjectConfigurationHandler<>("pvp.json", PvPDataModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("pvp.json"), PvpInitializer.class));

    @CommandNode("pvp on")
    private static int $on(@CommandSource ServerPlayerEntity player) {
        Set<String> whitelist = pvpHandler.model().whitelist;
        String name = player.getGameProfile().getName();
        if (!whitelist.contains(name)) {
            whitelist.add(name);
            pvpHandler.writeStorage();

            LocaleHelper.sendMessageByKey(player, "pvp.on");
            return CommandHelper.Return.SUCCESS;
        }

        LocaleHelper.sendMessageByKey(player, "pvp.on.already");
        return CommandHelper.Return.FAIL;
    }

    @CommandNode("pvp off")
    private static int $off(@CommandSource ServerPlayerEntity player) {
        Set<String> whitelist = pvpHandler.model().whitelist;
        String name = player.getGameProfile().getName();
        if (whitelist.contains(name)) {
            whitelist.remove(name);
            pvpHandler.writeStorage();

            LocaleHelper.sendMessageByKey(player, "pvp.off");
            return CommandHelper.Return.SUCCESS;
        }

        LocaleHelper.sendMessageByKey(player, "pvp.off.already");
        return CommandHelper.Return.FAIL;
    }

    @CommandNode("pvp status")
    private static int $status(@CommandSource ServerPlayerEntity player) {
        Set<String> whitelist = pvpHandler.model().whitelist;

        boolean flag = whitelist.contains(player.getGameProfile().getName());
        player.sendMessage(
            LocaleHelper.getTextByKey(player, "pvp.status")
                .copy()
                .append(LocaleHelper.getTextByKey(player, flag ? "on" : "off")));
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("pvp list")
    private static int $list(@CommandSource ServerCommandSource source) {
        Set<String> whitelist = pvpHandler.model().whitelist;
        LocaleHelper.sendMessageByKey(source, "pvp.list", whitelist);
        return CommandHelper.Return.SUCCESS;
    }

    public static boolean contains(String name) {
        return pvpHandler.model().whitelist.contains(name);
    }

}
