package io.github.sakurawald.module.initializer.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.extern.slf4j.Slf4j;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.Collection;


@Slf4j
public class TestInitializer extends ModuleInitializer {

    private static int clearChat(CommandContext<ServerCommandSource> ctx) {
        for (int i = 0; i < 50; i++) {
            ctx.getSource().sendMessage(Component.empty());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int magic(CommandContext<ServerCommandSource> ctx) {

        ServerPlayerEntity player = ctx.getSource().getPlayer();
        LuckPerms api = LuckPermsProvider.get();

        api.getEventBus().subscribe(NodeMutateEvent.class, (NodeMutateEvent event) -> {
            Collection<Node> nodes = event.getTarget().getNodes();
//            log.warn("before = {}, after = {}",event.getDataBefore(), event.getDataAfter());
            log.warn("nodes = {}", nodes);
        });

//        MetaNode node = MetaNode.builder("some-key", "some-value").build();
//        Group group = api.getGroupManager().getGroup("default");
//        group.data().add(node);
//        api.getGroupManager().saveGroup(group);

        String username = ctx.getSource().getPlayer().getGameProfile().getName();
        for (Node n : api.getUserManager().getUser(username).getDistinctNodes()) {
            log.warn("node is {}", n);
        }


        User user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);
        user.getCachedData().getPermissionData().getPermissionMap().forEach((k, v) -> {
            log.warn("key = {}, value = {}", k, v);
        });


        String metaValue = user.getCachedData().getMetaData().getMetaValue("fuji.flyspeed");
        log.warn("metaValue = {}", metaValue);
        player.getAbilities().setWalkSpeed(Float.valueOf(metaValue));
        player.sendAbilitiesUpdate();

        var source = ctx.getSource();
        TriState test = Permissions.getPermissionValue(source, "fuji.seed");
        source.sendMessage(Text.literal("state is " + test.name()));
        return 1;
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("test").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("clear-chat").executes(TestInitializer::clearChat))
                        .then(CommandManager.literal("magic").executes(TestInitializer::magic))
        );
    }
}
