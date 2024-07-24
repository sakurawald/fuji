package io.github.sakurawald.module.initializer.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonReaderUtils;

import java.util.*;


@Slf4j
public class TestInitializer extends ModuleInitializer {

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

    // Create and open a book about cats for the target audience
    public static void openMyBook(final @NonNull Audience target) {
        Component bookTitle = Component.text("Encyclopedia of cats");
        Component bookAuthor = Component.text("kashike");
        Collection<Component> bookPages = new ArrayList<>() {
            {
                this.add(Component.text("first" ));
                this.add(Component.text("second" ));
                this.add(Component.text("third" ));
            }
        };

        Book myBook = Book.book(bookTitle, bookAuthor, bookPages);
        target.openBook(myBook);
    }

    private static int $run(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = player.server;
        PlayerManager playerManager = server.getPlayerManager();


//        Random random = new Random();
//        ServerPlayerEntity serverPlayerEntity = TabListSortInitializer.makeServerPlayerEntity(server, String.valueOf(random.nextInt(1000)));
//        EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
//        playerManager.sendToAll(new PlayerListS2CPacket(enumSet, List.of(serverPlayerEntity)));

        return 1;
    }

    private static void extracted(MinecraftServer server) {
        /* make encoded player list */
        ArrayList<ServerPlayerEntity> encodedPlayers = new ArrayList<>();
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity p : players) {
            encodedPlayers.add(TabListSortInitializer.makeServerPlayerEntity(server, p));
        }

        /* update tab list name for encoded players */
        PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME), encodedPlayers);
        server.getPlayerManager().sendToAll(playerListS2CPacket);
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        registerStore();
        dispatcher.register(
                CommandManager.literal("test").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("run").executes(TestInitializer::$run))
        );
    }
}
