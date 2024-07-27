package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.head.privoder.HeadProvider;
import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

import static net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import static net.minecraft.server.command.CommandManager.literal;


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

    @SneakyThrows
    private static int $run(@NotNull CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = player.server;
        PlayerManager playerManager = server.getPlayerManager();

//        Registry<DimensionType> dimensionTypes = IdentifierHelper.ofRegistry(RegistryKeys.DIMENSION_TYPE);
//        for (DimensionType o : dimensionTypes) {
//            LogUtil.warn("id = {} \n o = {}", dimensionTypes.getId(o).toString(),o);
//        }

//        Registry<DimensionOptions> dimensionOptions = IdentifierHelper.ofRegistry(RegistryKeys.DIMENSION);
//        for (DimensionOptions dimensionOption : dimensionOptions) {
//            LogUtil.warn("dimension option id = {}", dimensionOptions.getId(dimensionOption));
//        }

        return -1;
    }

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        registerStore();
        dispatcher.register(
                literal("tester").requires(s -> s.hasPermissionLevel(4))
                        .then(literal("run").executes(TesterInitializer::$run))
        );

    }

}
