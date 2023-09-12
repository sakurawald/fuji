package fun.sakurawald.module.chat_style;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigGSON;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.chat_history.ChatHistoryModule;
import fun.sakurawald.module.main_stats.MainStats;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.minecraft.commands.Commands.argument;

@SuppressWarnings("resource")
public class ChatStyleModule {
    private static final MiniMessage miniMessage = MiniMessage.builder().build();

    private static final ScheduledExecutorService mentionExecutor = Executors.newScheduledThreadPool(1);

    public static LiteralCommandNode<CommandSourceStack> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        return dispatcher.register(
                Commands.literal("chat")
                        .then(argument("format", StringArgumentType.greedyString())
                                .executes(ChatStyleModule::setChatFormat)
                        ));
    }

    private static int setChatFormat(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        String name = player.getGameProfile().getName();
        String format = StringArgumentType.getString(ctx, "format");
        ConfigManager.chatWrapper.instance().player2format.put(name, format);
        ConfigManager.chatWrapper.saveToDisk();

        return 1;
    }


    private static Component resolvePositionTag(ServerPlayer source, Component component) {
        Component replacement = Component.text("%s (%d %d %d) %s".formatted(source.level().dimensionTypeId().location(),
                source.getBlockX(), source.getBlockY(), source.getBlockZ(), source.chunkPosition().toString())).color(NamedTextColor.GOLD);
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)pos(?=\\s|$)").replacement(replacement).build());
    }

    private static Component resolveItemTag(ServerPlayer source, Component component) {
        ItemStack itemStack = source.getMainHandItem();
        Component replacement =
                itemStack.getDisplayName().asComponent().hoverEvent(source.getMainHandItem().asHoverEvent());
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)item(?=\\s|$)").replacement(replacement).build());
    }

    @SuppressWarnings("PatternValidation")
    private static String resolveMentionTag(ServerPlayer source, String str) {
        /* resolve player tag */
        ArrayList<ServerPlayer> mentionedPlayers = new ArrayList<>();
        for (ServerPlayer player : ModMain.SERVER.getPlayerList().getPlayers()) {
            String name = player.getGameProfile().getName();
            if (!str.contains(name)) continue;

            str = str.replace(name, "<aqua>%s</aqua>".formatted(name));
            mentionedPlayers.add(player);
        }

        /* run mention player task */
        ConfigGSON.Modules.ChatStyle.MentionPlayer mentionPlayer = ConfigManager.configWrapper.instance().modules.chat_style.mention_player;
        Sound sound = Sound.sound(Key.key(mentionPlayer.sound), Sound.Source.MUSIC, mentionPlayer.volume, mentionPlayer.pitch);
        int limit = mentionPlayer.limit;
        MentionPlayersTask mentionPlayersTask = new MentionPlayersTask(mentionedPlayers, sound, limit);
        ScheduledFuture<?> scheduledFuture = mentionExecutor.scheduleAtFixedRate(mentionPlayersTask, 0, mentionPlayer.interval, TimeUnit.MILLISECONDS);
        mentionPlayersTask.setScheduledFuture(scheduledFuture);

        return str;
    }

    public static void handleChatMessage(ServerPlayer source, String message) {
        /* resolve format */
        message = ConfigManager.chatWrapper.instance().player2format.getOrDefault(source.getGameProfile().getName(), message)
                .replace("%message%", message);

        /* resolve stats */
        String input = ConfigManager.configWrapper.instance().modules.chat_style.format;
        message = resolveMentionTag(source, message);
        input = input.replace("%message%", message);
        input = input.replace("%player%", source.getGameProfile().getName());
        MainStats stats = MainStats.uuid2stats.getOrDefault(source.getUUID().toString(), new MainStats());
        stats.update(source);
        input = stats.resolve(input);

        /* resolve tags */
        Component component = miniMessage.deserialize(input, Formatter.date("date", LocalDateTime.now(ZoneId.systemDefault()))).asComponent();
        component = resolveItemTag(source, component);
        component = resolvePositionTag(source, component);
        ChatHistoryModule.CACHE.add(component);
        ModMain.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(component));
        // info so that it can be seen in the console
        for (ServerPlayer player : ModMain.SERVER.getPlayerList().getPlayers()) {
            player.sendMessage(component);
        }
    }

}
