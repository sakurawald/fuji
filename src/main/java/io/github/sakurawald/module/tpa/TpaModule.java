package io.github.sakurawald.module.tpa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.chat.mention.MentionPlayersJob;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;

@SuppressWarnings("LombokGetterMayBeUsed")
@Slf4j
public class TpaModule extends AbstractModule {

    @Getter
    private final ArrayList<TpaRequest> requests = new ArrayList<>();


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("tpa").then(argument("player", EntityArgument.player()).executes(this::$tpa))
        );
        dispatcher.register(
                Commands.literal("tpahere").then(argument("player", EntityArgument.player()).executes(this::$tpahere))
        );
        dispatcher.register(
                Commands.literal("tpaaccept").then(argument("player", EntityArgument.player()).executes(this::$tpaaccept))
        );
        dispatcher.register(
                Commands.literal("tpadeny").then(argument("player", EntityArgument.player()).executes(this::$tpadeny))
        );
        dispatcher.register(
                Commands.literal("tpacancel").then(argument("player", EntityArgument.player()).executes(this::$tpacancel))
        );
    }

    private int $tpa(CommandContext<CommandSourceStack> ctx) {
        return doRequest(ctx, false);
    }

    private int $tpahere(CommandContext<CommandSourceStack> ctx) {
        return doRequest(ctx, true);
    }

    private int $tpaaccept(CommandContext<CommandSourceStack> ctx) {
        return doResponse(ctx, ResponseStatus.ACCEPT);
    }

    private int $tpadeny(CommandContext<CommandSourceStack> ctx) {
        return doResponse(ctx, ResponseStatus.DENY);
    }

    private int $tpacancel(CommandContext<CommandSourceStack> ctx) {
        return doResponse(ctx, ResponseStatus.CANCEL);
    }

    @SuppressWarnings("SameReturnValue")
    private int doResponse(CommandContext<CommandSourceStack> ctx, ResponseStatus status) {
        ServerPlayer source = ctx.getSource().getPlayer();
        if (source == null) return Command.SINGLE_SUCCESS;

        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            MessageUtil.sendActionBar(source, "tpa.player_not_found");
            return Command.SINGLE_SUCCESS;
        }

        /* resolve relative request */
        Optional<TpaRequest> requestOptional = requests.stream()
                .filter(request ->
                        status == ResponseStatus.CANCEL ?
                                (request.getSender().equals(source) && request.getReceiver().equals(player))
                                : (request.getSender().equals(player) && request.getReceiver().equals(source)))
                .findFirst();
        if (requestOptional.isEmpty()) {
            MessageUtil.sendActionBar(source, "tpa.no_relative_ticket");
            return Command.SINGLE_SUCCESS;
        }

        TpaRequest request = requestOptional.get();
        if (status == ResponseStatus.ACCEPT) {
            request.getSender().sendActionBar(request.asSenderComponent$Accepted());
            request.getReceiver().sendMessage(request.asReceiverComponent$Accepted());

            ServerPlayer who = request.getTeleportWho();
            ServerPlayer to = request.getTeleportTo();
            MentionPlayersJob.scheduleJob(request.isTpahere() ? to : who);
            who.teleportTo((ServerLevel) to.level(), to.getX(), to.getY(), to.getZ(), to.getYRot(), to.getXRot());
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
    }

    @SuppressWarnings("SameReturnValue")
    private int doRequest(CommandContext<CommandSourceStack> ctx, boolean tpahere) {
        ServerPlayer source = ctx.getSource().getPlayer();
        if (source == null) return Command.SINGLE_SUCCESS;
        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            MessageUtil.sendActionBar(source, "tpa.player_not_found");
            return Command.SINGLE_SUCCESS;
        }

        /* add request */
        TpaRequest request = new TpaRequest(source, player, tpahere);

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
    }


    private enum ResponseStatus {ACCEPT, DENY, CANCEL}
}
