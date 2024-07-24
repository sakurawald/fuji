package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import lombok.NonNull;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

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

    // Create and open a book about cats for the target audience
    public static void openMyBook(final @NonNull Audience target) {
        Component bookTitle = Component.text("Encyclopedia of cats");
        Component bookAuthor = Component.text("kashike");
        Collection<Component> bookPages = new ArrayList<>() {
            {
                this.add(Component.text("first"));
                this.add(Component.text("second"));
                this.add(Component.text("third"));
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

        return -1;
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
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        registerStore();
        dispatcher.register(
                literal("tester").requires(s -> s.hasPermissionLevel(4))
                        .then(literal("run").executes(TesterInitializer::$run))
        );

    }

}
