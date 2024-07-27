package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.MapCodec;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.head.api.HeadDatabaseAPI;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URL;
import java.util.*;

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

        URI url = URI.create("https://raw.githubusercontent.com/sakurawald/fuji-fabric/dev/assets/module/head/food-drinks.json");
        FileUtils.copyURLToFile(url.toURL() , HeadDatabaseAPI.getSTORAGE_PATH().resolve("test.json").toFile());
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
