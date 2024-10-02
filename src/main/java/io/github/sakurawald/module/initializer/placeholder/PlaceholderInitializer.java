package io.github.sakurawald.module.initializer.placeholder;

import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.DateUtil;
import io.github.sakurawald.core.auxiliary.RandomUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.placeholder.gui.PlaceholderGui;
import io.github.sakurawald.module.initializer.placeholder.job.UpdateSumUpPlaceholderJob;
import io.github.sakurawald.module.initializer.placeholder.structure.SumUpPlaceholder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@CommandNode("placeholder")
@CommandRequirement(level = 4)
public class PlaceholderInitializer extends ModuleInitializer {
    private final Map<String, Map<String, String>> rotate = new HashMap<>();

    private static final Text NO_PLAYER = Text.literal("[NO-PLAYER]");
    private static final Text NO_SERVER = Text.literal("[NO-SERVER]");
    private static final Text INVALID = Text.literal("[INVALID]");

    private static final Pattern ESCAPE_PARSER = Pattern.compile("\\s*([\\s\\S]+)\\s+(\\d+)\\s*");

    @CommandNode("list")
    private static int list(@CommandSource ServerPlayerEntity player) {
        List<Identifier> list = Placeholders.getPlaceholders().keySet().stream().toList();
        new PlaceholderGui(player, list, 0).open();
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("parse")
    private static int list(@CommandSource ServerCommandSource source
        , Optional<ServerPlayerEntity> player
        , GreedyString input) {
        ServerPlayerEntity target = player.orElse(null);

        Text text = LocaleHelper.getTextByValue(target, input.getValue());
        source.sendMessage(text);
        return CommandHelper.Return.SUCCESS;
    }

    @Override
    public void onInitialize() {
        /* register placeholders */
        registerPlayerMinedPlaceholder();
        registerServerMinedPlaceholder();

        registerPlayerPlacedPlaceholder();
        registerServerPlacedPlaceholder();

        registerPlayerKilledPlaceholder();
        registerServerKilledPlaceholder();


        registerPlayerMovedPlaceholder();
        registerServerMovedPlaceholder();

        registerPlayerPlaytimePlaceholder();
        registerServerPlaytimePlaceholder();

        registerHealthBarPlaceholder();
        registerRotatePlaceholder();
        registerHasPermissionPlaceholder();
        registerGetMetaPlaceholder();
        registerRandomPlayerPlaceholder();
        registerRandomPlaceholder();
        registerEscapePlaceholder();
        registerProtectPlaceholder();
        registerDatePlaceholder();

        /* events */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SumUpPlaceholder.ofServer();
            new UpdateSumUpPlaceholderJob().schedule();
        });
    }

    private void registerDatePlaceholder() {
        serverPlaceholder("date", (server, arg) -> {
            if (arg == null || arg.isEmpty()) {
                return Text.literal(DateUtil.getCurrentDate());
            }

            try {
                String currentDate = DateUtil.getCurrentDate(new SimpleDateFormat(arg));
                return Text.literal(currentDate);
            } catch (Exception e) {
                return Text.of("Invalid date formatter: " + arg);
            }
        });
    }

    private void registerEscapePlaceholder() {
        serverPlaceholder("escape", (server, args) -> {
            if (args == null) return INVALID;

            Matcher matcher = ESCAPE_PARSER.matcher(args);
            if (matcher.find()) {
                String placeholder = matcher.group(1);
                int level = Integer.parseInt(matcher.group(2));

                if (level == 1) return Text.literal("%" + placeholder + "%");
                if (level > 1)
                    return Text.literal("%fuji:escape " + placeholder + " " + (level - 1) + "%");
            }
            return Text.literal("%" + args + "%");
        });
    }

    private void registerProtectPlaceholder() {
        serverPlaceholder("protect", (server, args) -> {
            if (args == null) return Text.empty();
            return Text.literal(args);
        });
    }

    private void registerHasPermissionPlaceholder() {
        playerPlaceholder("has_permission", (player, args) -> {
            boolean value = PermissionHelper.hasPermission(player.getUuid(), args);
            return Text.literal(String.valueOf(value));
        });
    }

    private void registerGetMetaPlaceholder() {
        playerPlaceholder("get_meta", (player, args) -> {
            Optional<String> metaValue = PermissionHelper.getMeta(player.getUuid(), args, String::valueOf);
            return Text.literal(metaValue.orElse("META_NOT_FOUND"));
        });
    }

    private void registerRandomPlayerPlaceholder() {
        serverPlaceholder("random_player", (server, args) -> {
            List<ServerPlayerEntity> playerList = ServerHelper.getPlayers();
            ServerPlayerEntity serverPlayerEntity = RandomUtil.drawList(playerList);
            return Text.literal(serverPlayerEntity.getGameProfile().getName());
        });
    }

    private void registerRandomPlaceholder() {
        serverPlaceholder("random", (server, args) -> {
            if (args == null) return INVALID;

            String[] split = args.split(" ");
            if (split.length != 2) return INVALID;

            int i;
            try {
                i = RandomUtil.getRandom().nextInt(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            } catch (Exception e) {
                return INVALID;
            }

            return Text.literal(String.valueOf(i));
        });
    }

    private void registerHealthBarPlaceholder() {
        playerPlaceholder("health_bar", (player -> {
            int totalHearts = 10;
            int filledHearts = (int) (player.getHealth() / 2);
            int unfilledHearts = totalHearts - filledHearts;
            String str = "♥".repeat(filledHearts) + "♡".repeat(unfilledHearts);
            return Text.literal(str);
        }));
    }

    private void registerRotatePlaceholder() {
        Placeholders.register(Identifier.of(Fuji.MOD_ID, "rotate"), (ctx, args) -> {
            String namespace = "default";
            if (ctx.player() != null) {
                namespace = ctx.player().getGameProfile().getName();
            }

            rotate.putIfAbsent(namespace, new HashMap<>());
            Map<String, String> rotateMap = rotate.get(namespace);
            rotateMap.putIfAbsent(args, args);

            String frame = rotateMap.get(args);
            rotateMap.put(args, StringUtils.rotate(frame, -1));

            return PlaceholderResult.value(Text.literal(frame));
        });
    }

    private static void registerServerPlaytimePlaceholder() {
        serverPlaceholder("server_playtime", server -> Text.literal(String.valueOf(SumUpPlaceholder.ofServer().playtime)));
    }

    private static void registerPlayerPlaytimePlaceholder() {
        playerPlaceholder("player_playtime", player -> Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(player.getUuidAsString()).playtime)));
    }

    private static void registerServerMovedPlaceholder() {
        serverPlaceholder("server_moved", server -> Text.literal(String.valueOf(SumUpPlaceholder.ofServer().moved)));
    }

    private static void registerPlayerMovedPlaceholder() {
        playerPlaceholder("player_moved", player -> Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(player.getUuidAsString()).moved)));
    }

    private static void registerServerKilledPlaceholder() {
        serverPlaceholder("server_killed", server -> Text.literal(String.valueOf(SumUpPlaceholder.ofServer().killed)));
    }

    private static void registerPlayerKilledPlaceholder() {
        playerPlaceholder("player_killed", player -> Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(player.getUuidAsString()).killed)));
    }

    private static void registerServerPlacedPlaceholder() {
        serverPlaceholder("server_placed", server -> Text.literal(String.valueOf(SumUpPlaceholder.ofServer().placed)));
    }

    private static void registerPlayerPlacedPlaceholder() {
        playerPlaceholder("player_placed", player -> Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(player.getUuidAsString()).placed)));
    }

    private static void registerServerMinedPlaceholder() {
        serverPlaceholder("server_mined", (server) -> Text.literal(String.valueOf(SumUpPlaceholder.ofServer().mined)));
    }

    private static void registerPlayerMinedPlaceholder() {
        playerPlaceholder("player_mined", (player -> Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(player.getUuidAsString()).mined))));
    }

    @SuppressWarnings("resource")
    private static void serverPlaceholder(String name, BiFunction<MinecraftServer, String, Text> function) {
        PlaceholderHandler placeholderHandler = (ctx, arg) -> {
            if (ctx.server() == null) return PlaceholderResult.value(NO_SERVER);
            return PlaceholderResult.value(function.apply(ctx.server(), arg));
        };

        Placeholders.register(Identifier.of(Fuji.MOD_ID, name), placeholderHandler);
    }

    private static void playerPlaceholder(String name, BiFunction<ServerPlayerEntity, String, Text> function) {
        PlaceholderHandler placeholderHandler = (ctx, arg) -> {
            if (ctx.player() == null) return PlaceholderResult.value(NO_PLAYER);
            return PlaceholderResult.value(function.apply(ctx.player(), arg));
        };

        Placeholders.register(Identifier.of(Fuji.MOD_ID, name), placeholderHandler);
    }

    private static void serverPlaceholder(String name, Function<MinecraftServer, Text> function) {
        serverPlaceholder(name, (server, args) -> function.apply(server));
    }

    private static void playerPlaceholder(String name, Function<ServerPlayerEntity, Text> function) {
        playerPlaceholder(name, (player, args) -> function.apply(player));
    }

}
