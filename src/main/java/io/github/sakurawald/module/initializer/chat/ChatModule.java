package io.github.sakurawald.module.initializer.chat;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.display.DisplayHelper;
import io.github.sakurawald.module.initializer.chat.mention.MentionPlayersJob;
import io.github.sakurawald.module.initializer.main_stats.MainStats;
import io.github.sakurawald.module.initializer.main_stats.MainStatsModule;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ChatModule extends ModuleInitializer {
    private static final Pattern xaero_waypoint_pattern = Pattern.compile(
        "^xaero-waypoint:([^:]+):.+:(-?\\d+):(~?-?\\d*):(-?\\d+).*?-(.*)-waypoints$");
    private static final Pattern pos_pattern = Pattern.compile("^xaero-waypoint:|pos");
    private final MiniMessage miniMessage = MiniMessage.builder().build();
    private final MainStatsModule mainStatsModule = ModuleManager.getInitializer(MainStatsModule.class);
    @Getter
    private Queue<Component> chatHistory;

    @Override
    public void onInitialize() {
        Configs.chatHandler.loadFromDisk();

        chatHistory = EvictingQueue.create(Configs.configHandler.model().modules.chat.history.cache_size);
    }


    @Override
    public void onReload() {
        Configs.chatHandler.loadFromDisk();

        EvictingQueue<Component> newQueue = EvictingQueue.create(Configs.configHandler.model().modules.chat.history.cache_size);
        newQueue.addAll(chatHistory);
        chatHistory.clear();
        chatHistory = newQueue;
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("chat")
                .then(Commands.literal("format")
                    .then(Commands.literal("reset")
                        .executes(this::resetFormat)
                    )
                    .then(Commands.literal("set")
                        .then(argument("format", StringArgumentType.greedyString())
                            .executes(this::format)
                        )
                    )
                )
        );
    }


    private int format(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            String name = player.getGameProfile().getName();
            String format = StringArgumentType.getString(ctx, "format");
            Configs.chatHandler.model().format.player2format.put(name, format);
            Configs.chatHandler.saveToDisk();
            format = MessageUtil.ofString(player,"chat.format.set").replace("%s",format);
            Component formatComponent = miniMessage.deserialize(format, Formatter.date("date", LocalDateTime.now(ZoneId.systemDefault()))).asComponent();
            Component component = formatComponent
                .replaceText("%player%",Component.text(name))
                .replaceText("%message%",MessageUtil.ofComponent(player, "chat.format.show"));
            player.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        });
    }


    private int resetFormat(CommandContext<CommandSourceStack> ctx) 
    {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            String name = player.getGameProfile().getName();
            Configs.chatHandler.model().format.player2format.remove(name);
            Configs.chatHandler.saveToDisk();
            MessageUtil.sendMessage(player, "chat.format.reset");
            return Command.SINGLE_SUCCESS;
        });
    }

    private Component resolvePositionTag(ServerPlayer player, Component component) {
        String str = PlainTextComponentSerializer.plainText().serialize(component);
        Matcher posmatcher = pos_pattern.matcher(str);
        if (posmatcher.find()) {
            String hoverText;
            String click_command;
            int x,z;
            String y;
            String dim_name;
            Matcher xaeroMap_matcher = xaero_waypoint_pattern.matcher(str);
            if (xaeroMap_matcher.find()) { //小地图路径点
                hoverText = xaeroMap_matcher.group(1).replaceAll("^col^",":").replaceAll("^ast^","*"); //xaeroMap使用^col^代替:,^ast^代替*以确保解析正确
                x = Integer.parseInt(xaeroMap_matcher.group(2));
                y = xaeroMap_matcher.group(3);
                z = Integer.parseInt(xaeroMap_matcher.group(4));
                dim_name = xaeroMap_matcher.group(5).replaceFirst(".*\\$","").replaceAll("-","_"); //在部分自定义纬度,xaeroMap使用类似dim%minecraft$开头
                click_command = str
                    .replaceFirst("xaero-waypoint","/xaero_waypoint_add") //小地图分享格式：xaero-waypoint:name:n:0:~:0:0:false:0:Internal-overworld-waypoints
                    .replaceAll(":Internal-",":Internal_")               //小地图指令格式：/xaero_waypoint:name:n:0:~:0:0:false:0:Internal_overworld_waypoints
                    .replaceAll("-waypoints$","_waypoints");
            } else {
                hoverText = MessageUtil.ofString(player,"chat.current_pos");
                dim_name = player.serverLevel().dimension().location().toString().replaceFirst("minecraft:","");
                x = player.getBlockX();
                y = Integer.toString(player.getBlockY());
                z = player.getBlockZ();
                click_command = MessageUtil.ofString(player,"chat.xaero_waypoint_add.command",x, y, z,dim_name.replaceAll(":","$"));
            }
            switch (dim_name) {
                case "overworld":
                    hoverText += "\n"+MessageUtil.ofString(player,"the_nether")
                            +": %d %s %d".formatted(x/8, y, z/8);
                    break;
                case "the_nether":
                    hoverText += "\n"+MessageUtil.ofString(player,"overworld")
                        +": %d %s %d".formatted(x*8, y, z*8);  
                    break;
            }
            String dim_display_name;
            if (MessageUtil.containsKey(player,dim_name)) {
                dim_display_name = MessageUtil.ofString(player,dim_name);
            } else {
                dim_display_name = dim_name;
            }
            Component replacement = Component.text("[%d %s %d, %s]".formatted(x, y, z, dim_display_name))
                    .decoration(TextDecoration.ITALIC, true)
                    .clickEvent(ClickEvent.runCommand(click_command))
                    .hoverEvent(Component.text(hoverText+"\n").append(MessageUtil.ofComponent(player,"chat.xaero_waypoint_add")));
            return component.replaceText(TextReplacementConfig.builder()
                .match("^xaero-waypoint:.*|pos")
                .replacement(replacement)
                .build());
        }
        return component;
    }

    private Component resolveItemTag(ServerPlayer player, Component component) {
        String displayUUID = DisplayHelper.createItemDisplay(player);
        Component replacement =
                player.getMainHandItem().getDisplayName().asComponent()
                        .hoverEvent(MessageUtil.ofComponent(player, "display.click.prompt"))
                        .clickEvent(displayCallback(displayUUID));
        return component.replaceText(TextReplacementConfig.builder().match("item").replacement(replacement).build());
    }

    private Component resolveInvTag(ServerPlayer player, Component component) {
        String displayUUID = DisplayHelper.createInventoryDisplay(player);
        Component replacement =
                MessageUtil.ofComponent(player, "display.inventory.text")
                        .hoverEvent(MessageUtil.ofComponent(player, "display.click.prompt"))
                        .clickEvent(displayCallback(displayUUID));
        return component.replaceText(TextReplacementConfig.builder().match("inv").replacement(replacement).build());
    }

    private Component resolveEnderTag(ServerPlayer player, Component component) {
        String displayUUID = DisplayHelper.createEnderChestDisplay(player);
        Component replacement =
                MessageUtil.ofComponent(player, "display.ender_chest.text")
                        .hoverEvent(MessageUtil.ofComponent(player, "display.click.prompt"))
                        .clickEvent(displayCallback(displayUUID));
        return component.replaceText(TextReplacementConfig.builder().match("ender").replacement(replacement).build());
    }

    @NotNull
    private ClickEvent displayCallback(String displayUUID) {
        return ClickEvent.callback(audience -> {
            if (audience instanceof CommandSourceStack css && css.getPlayer() != null) {
                DisplayHelper.viewDisplay(css.getPlayer(), displayUUID);
            }
        }, ClickCallback.Options.builder().lifetime(Duration.of(Configs.configHandler.model().modules.chat.display.expiration_duration_s, ChronoUnit.SECONDS))
                .uses(Integer.MAX_VALUE).build());
    }

    private Component resolveMentionTag(ServerPlayer player, Component component) {
        /* resolve player tag */
        ArrayList<ServerPlayer> mentionedPlayers = new ArrayList<>();
        String[] playerNames = Fuji.SERVER.getPlayerNames();
        Arrays.sort(playerNames, Comparator.comparingInt(String::length).reversed());
        String str = PlainTextComponentSerializer.plainText().serialize(component);
        for (String playerName : playerNames) {
            Pattern pattern = Pattern.compile("(?:(?<=\\s)|^|@)" + Pattern.quote(playerName));
            Matcher matcher = pattern.matcher(str);
            if (!matcher.find()) continue;
            component = component.replaceText(matcher.group(),
                Component.text("@",NamedTextColor.GREEN)
                .append(Component.text(playerName,NamedTextColor.DARK_GREEN)));
            mentionedPlayers.add(Fuji.SERVER.getPlayerList().getPlayerByName(playerName));
        }

        /* run mention player task */
        if (!mentionedPlayers.isEmpty()) {
            MentionPlayersJob.scheduleJob(mentionedPlayers);
        }

        return component;

    }

    public Component resolveLinks(ServerPlayer player, Component component) { 
        
        String str = PlainTextComponentSerializer.plainText().serialize(component);

        //BV号替换
        Matcher bvmatcher = Pattern.compile("(?<=[^/]|^)BV\\w{10}",Pattern.CASE_INSENSITIVE).matcher(str);
        while (bvmatcher.find()) {
            String bvNumber = bvmatcher.group();
            Component textBuilder = Component.text("bilibili")
                .decoration(TextDecoration.UNDERLINED, true)
                .hoverEvent(HoverEvent.showText(Component.text(bvNumber)))
                .clickEvent(ClickEvent.openUrl("https://www.bilibili.com/video/"+bvNumber));
            component = component.replaceText(bvNumber, textBuilder);
        }

        //网址替换
        Matcher urlmatcher = Pattern.compile("(https?)://[^\\s/$.?#].[^\\s]*").matcher(str);
        Pattern displayPattern = Pattern.compile("(?<=https?://)[^/\\s]{0,15}(?=\\.[^./]{0,20}(/|$))");
        while (urlmatcher.find()) {
            String url = urlmatcher.group();
            Matcher displayMatcher = displayPattern.matcher(url);
            String displayText = url;
            if (displayMatcher.find()) {
                displayText = displayMatcher.group().replaceFirst("www.|m.","");
            }
            Component textBuilder = Component.text(displayText)
                .decoration(TextDecoration.UNDERLINED, true)
                .hoverEvent(HoverEvent.showText(Component.text(url)))
                .clickEvent(ClickEvent.openUrl(url));
            component = component.replaceText(url, textBuilder);
        }
        
        return component;
    }

    public void broadcastChatMessage(ServerPlayer player, String message) {

        //输入文本处理
        Component msgComponent = Component.text(message);
        msgComponent = resolveLinks(player, msgComponent);
        msgComponent = resolveMentionTag(player, msgComponent);
        msgComponent = resolveItemTag(player, msgComponent);
        msgComponent = resolveInvTag(player, msgComponent);
        msgComponent = resolveEnderTag(player, msgComponent);
        msgComponent = resolvePositionTag(player, msgComponent);

        //玩家发言样式
        String format = Configs.chatHandler.model().format.player2format.getOrDefault(
            player.getGameProfile().getName(),                  //获取玩家名对应的样式
            Configs.configHandler.model().modules.chat.format); //若无，使用默认样式
        /* resolve stats */
        if (mainStatsModule != null) {
            MainStats stats = MainStats.uuid2stats.getOrDefault(player.getUUID().toString(), new MainStats());
            format = stats.update(player).resolve(Fuji.SERVER, format);
        }
        Component formatComponent = miniMessage.deserialize(
            format, Formatter.date("date", LocalDateTime.now(ZoneId.systemDefault()))).asComponent();
        //合并
        Component component = formatComponent
            .replaceText("%player%",Component.text(player.getGameProfile().getName()))
            .replaceText("%message%",msgComponent);

        chatHistory.add(component);
        // info so that it can be seen in the console
        Fuji.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(component));
        for (ServerPlayer receiver : Fuji.SERVER.getPlayerList().getPlayers()) {
            receiver.sendMessage(component);
        }
    }

}
