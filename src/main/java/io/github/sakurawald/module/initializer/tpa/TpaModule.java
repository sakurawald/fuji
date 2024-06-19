package io.github.sakurawald.module.initializer.tpa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.mention.MentionPlayersJob;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.ArrayList;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;

@SuppressWarnings("LombokGetterMayBeUsed")

public class TpaModule extends ModuleInitializer {

    @Getter
    private final ArrayList<TpaRequest> requests = new ArrayList<>();

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("tpa").then(argument("player", EntityArgumentType.player()).executes(this::$tpa))
        );
        dispatcher.register(
                CommandManager.literal("tpahere").then(argument("player", EntityArgumentType.player()).executes(this::$tpahere))
        );
        dispatcher.register(
                CommandManager.literal("tpaaccept").then(argument("player", EntityArgumentType.player()).executes(this::$tpaaccept))
        );
        dispatcher.register(
                CommandManager.literal("tpadeny").then(argument("player", EntityArgumentType.player()).executes(this::$tpadeny))
        );
        dispatcher.register(
                CommandManager.literal("tpacancel").then(argument("player", EntityArgumentType.player()).executes(this::$tpacancel))
        );
    }

    private int $tpa(CommandContext<ServerCommandSource> ctx) {
        return doRequest(ctx, false);
    }

    private int $tpahere(CommandContext<ServerCommandSource> ctx) {
        return doRequest(ctx, true);
    }

    private int $tpaaccept(CommandContext<ServerCommandSource> ctx) {
        return doResponse(ctx, ResponseStatus.ACCEPT);
    }

    private int $tpadeny(CommandContext<ServerCommandSource> ctx) {
        return doResponse(ctx, ResponseStatus.DENY);
    }

    private int $tpacancel(CommandContext<ServerCommandSource> ctx) {
        return doResponse(ctx, ResponseStatus.CANCEL);
    }

    @SuppressWarnings("SameReturnValue")
    private int doResponse(CommandContext<ServerCommandSource> ctx, ResponseStatus status) {
        return CommandUtil.playerOnlyCommand(ctx, source -> {
            ServerPlayerEntity target;
            try {
                target = EntityArgumentType.getPlayer(ctx, "player");
            } catch (CommandSyntaxException e) {
                MessageUtil.sendActionBar(source, "tpa.player_not_found");
                return Command.SINGLE_SUCCESS;
            }

            /* resolve relative request */
            Optional<TpaRequest> requestOptional = requests.stream()
                    .filter(request ->
                            status == ResponseStatus.CANCEL ?
                                    (request.getSender().equals(source) && request.getReceiver().equals(target))
                                    : (request.getSender().equals(target) && request.getReceiver().equals(source)))
                    .findFirst();
            if (requestOptional.isEmpty()) {
                MessageUtil.sendActionBar(source, "tpa.no_relative_ticket");
                return Command.SINGLE_SUCCESS;
            }

            TpaRequest request = requestOptional.get();
            if (status == ResponseStatus.ACCEPT) {
                request.getSender().sendActionBar(request.asSenderComponent$Accepted());
                request.getReceiver().sendMessage(request.asReceiverComponent$Accepted());

                ServerPlayerEntity who = request.getTeleportWho();
                ServerPlayerEntity to = request.getTeleportTo();
                MentionPlayersJob.scheduleJob(request.isTpahere() ? to : who);
                who.teleport((ServerWorld) to.getWorld(), to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
            } else if (status == ResponseStatus.DENY) {
                request.getSender().sendActionBar(request.asSenderComponent$Denied());
                request.getReceiver().sendMessage(request.asReceiverComponent$Denied());
            } else if (status == ResponseStatus.CANCEL) {
                request.getSender().sendMessage(request.asSenderComponent$Cancelled());
                request.getReceiver().sendMessage(request.asReceiverComponent$Cancelled());
            }

            request.cancelTimeout();
            requests.remove(request);
            return Command.SINGLE_SUCCESS;
        });
    }

    @SuppressWarnings("SameReturnValue")
    private int doRequest(CommandContext<ServerCommandSource> ctx, boolean tpahere) {
        return CommandUtil.playerOnlyCommand(ctx, source -> {
            ServerPlayerEntity target;
            try {
                target = EntityArgumentType.getPlayer(ctx, "player");
            } catch (CommandSyntaxException e) {
                MessageUtil.sendActionBar(source, "tpa.player_not_found");
                return Command.SINGLE_SUCCESS;
            }

            /* add request */
            TpaRequest request = new TpaRequest(source, target, tpahere);

            /* has similar request ? */
            if (request.getSender().equals(request.getReceiver())) {
                MessageUtil.sendActionBar(request.getSender(), "tpa.request_to_self");

                return Command.SINGLE_SUCCESS;
            }

            if (requests.stream().anyMatch(request::similarTo)) {
                MessageUtil.sendActionBar(request.getSender(), "tpa.similar_request_exists");
                return Command.SINGLE_SUCCESS;
            }

            requests.add(request);
            request.startTimeout();

            /* feedback */
            request.getReceiver().sendMessage(request.asReceiverComponent$Sent());
            MentionPlayersJob.scheduleJob(request.getReceiver());
            request.getSender().sendMessage(request.asSenderComponent$Sent());
            return Command.SINGLE_SUCCESS;
        });
    }

    private enum ResponseStatus {ACCEPT, DENY, CANCEL}
}
