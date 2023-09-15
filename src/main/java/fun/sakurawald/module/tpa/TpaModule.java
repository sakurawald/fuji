package fun.sakurawald.module.tpa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

import static net.minecraft.commands.Commands.argument;

public class TpaModule {
    private static final ArrayList<TpaRequest> requests = new ArrayList<>();

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
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
                Commands.literal("tpadeny").then(argument("player", EntityArgument.player()).executes(TpaModule::$tpacancel))
        );
    }

    private static int $tpa(CommandContext<CommandSourceStack> ctx) {
        return doRequest(ctx, false);
    }

    private static int $tpahere(CommandContext<CommandSourceStack> ctx) {
        return doRequest(ctx, true);
    }

    private static int $tpaaccept(CommandContext<CommandSourceStack> ctx) {


        return 0;
    }

    private static int $tpadeny(CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int $tpacancel(CommandContext<CommandSourceStack> ctx) {

        return 0;
    }

    private static int doRequest(CommandContext<CommandSourceStack> ctx, boolean tpahere) {
        ServerPlayer source = ctx.getSource().getPlayer();
        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        TpaRequest request = new TpaRequest(source, player, false);
        requests.add(request);

        request.getSender().sendMessage(request.asSenderComponent$Sent());
        request.getReceiver().sendMessage(request.asReceiverComponent$Sent());
        return Command.SINGLE_SUCCESS;
    }
}
