package fun.sakurawald.module.tpa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fun.sakurawald.module.chat_style.MentionPlayersTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;

@Slf4j
public class TpaModule {

    @Getter
    private static final ArrayList<TpaRequest> requests = new ArrayList<>();

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("tpa").then(argument("player", EntityArgument.player()).executes(TpaModule::$tpa))
        );
        dispatcher.register(
                Commands.literal("tpahere").then(argument("player", EntityArgument.player()).executes(TpaModule::$tpahere))
        );
        dispatcher.register(
                Commands.literal("tpaaccept").then(argument("player", EntityArgument.player()).executes(TpaModule::$tpaaccept))
        );
        dispatcher.register(
                Commands.literal("tpadeny").then(argument("player", EntityArgument.player()).executes(TpaModule::$tpadeny))
        );
        dispatcher.register(
                Commands.literal("tpacancel").then(argument("player", EntityArgument.player()).executes(TpaModule::$tpacancel))
        );
    }

    private static int $tpa(CommandContext<CommandSourceStack> ctx) {
        return doRequest(ctx, false);
    }

    private static int $tpahere(CommandContext<CommandSourceStack> ctx) {
        return doRequest(ctx, true);
    }

    private static int $tpaaccept(CommandContext<CommandSourceStack> ctx) {
        return doResponse(ctx, ResponseStatus.ACCEPT);
    }

    private static int $tpadeny(CommandContext<CommandSourceStack> ctx) {
        return doResponse(ctx, ResponseStatus.DENY);
    }

    private static int $tpacancel(CommandContext<CommandSourceStack> ctx) {
        return doResponse(ctx, ResponseStatus.CANCEL);
    }

    @SuppressWarnings("SameReturnValue")
    private static int doResponse(CommandContext<CommandSourceStack> ctx, ResponseStatus status) {
        ServerPlayer source = ctx.getSource().getPlayer();
        if (source == null) return Command.SINGLE_SUCCESS;

        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            source.sendActionBar(Component.text("Player not found", NamedTextColor.RED));
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
            source.sendActionBar(Component.text("No relative request.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        TpaRequest request = requestOptional.get();
        if (status == ResponseStatus.ACCEPT) {
            request.getSender().sendActionBar(request.asSenderComponent$Accepted());
            request.getReceiver().sendMessage(request.asReceiverComponent$Accepted());
            ServerPlayer who = request.getTeleportWho();
            ServerPlayer to = request.getTeleportTo();
            new MentionPlayersTask(who).startTask();
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
    private static int doRequest(CommandContext<CommandSourceStack> ctx, boolean tpahere) {
        ServerPlayer source = ctx.getSource().getPlayer();
        if (source == null) return Command.SINGLE_SUCCESS;
        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            source.sendActionBar(Component.text("Player not found", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        /* add request */
        TpaRequest request = new TpaRequest(source, player, tpahere);

        /* has similar request ? */
        if (request.getSender().equals(request.getReceiver())) {
            request.getSender().sendActionBar(Component.text("You can't send a request to yourself.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        if (requests.stream().anyMatch(request::similarTo)) {
            request.getSender().sendActionBar(Component.text("A similar request already exists.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        requests.add(request);
        request.startTimeout();

        /* feedback */
        request.getReceiver().sendMessage(request.asReceiverComponent$Sent());
        new MentionPlayersTask(request.getReceiver()).startTask();
        request.getSender().sendMessage(request.asSenderComponent$Sent());
        return Command.SINGLE_SUCCESS;
    }

    private enum ResponseStatus {ACCEPT, DENY, CANCEL}
}
