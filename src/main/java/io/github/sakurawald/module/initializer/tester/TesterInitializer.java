package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.command.processor.BrigadierAnnotationProcessor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.*;


public class TesterInitializer extends ModuleInitializer {

    void registerStore() {
        Placeholders.register(Identifier.of("fuji", "store"), (ctx, args) -> {
            if (args.isEmpty()) {
                return PlaceholderResult.invalid();

            }

            /**
             * building block -> set, get, if, recur, arithmetic, boolean
             * - %fuji:store {name} {value}%
             * - (set {name} {value})
             * - (get {name})
             * - (cmd {cmd})
             * - (eval {list})
             */

            return null;
        });


    }

    //    @SneakyThrows
    private static int $run(@NotNull CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = player.server;
        PlayerManager playerManager = server.getPlayerManager();

        ItemStack mainHandStack = player.getMainHandStack();

        player.sendMessage(Text.literal("the value is ${3+2}"));

        return -1;
    }

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        dispatcher.register(
                literal("tester").requires(s -> s.hasPermissionLevel(4))
                        .then(literal("run").executes(TesterInitializer::$run))
        );

//        List<ArgumentBuilder<ServerCommandSource, ?>> argument = new ArrayList<>(){
//            {
//                this.add(literal("create"));
//                this.add(argument("name", StringArgumentType.string()));
//                this.add(literal("age"));
//                this.add(argument("age", IntegerArgumentType.integer()));
//                this.add(literal("finish"));
//            }
//
//        };
//
//        LiteralArgumentBuilder<ServerCommandSource> ret = BrigadierAnnotationProcessor.makeRootArgumentBuilder(argument, (root) -> {
//            return root.executes(ctx -> {
//                ctx.getSource().sendMessage(Text.of("Hello !"));
//                return 1;
//            });
//        });
//
//        dispatcher.register(ret);

    }

}
