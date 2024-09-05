package io.github.sakurawald.module.initializer.temp_ban;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GameProfileCollection;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandNode("temp-ban")
@CommandRequirement(level = 4)
public class TempBanInitializer extends ModuleInitializer {

    @CommandNode("ip")
    int ip(@CommandSource CommandContext<ServerCommandSource> ctx, String ip, String expiry, GreedyString reason) throws CommandSyntaxException {

        if (!InetAddresses.isInetAddress(ip)) {
            throw new SimpleCommandExceptionType(Text.translatable("commands.banip.invalid")).create();
        }

        // add
        Date expire = parseDate(expiry);
        ServerCommandSource serverCommandSource = ctx.getSource();
        BannedIpEntry bannedIpEntry = new BannedIpEntry(ip, null, ctx.getSource().getName(), expire, reason.getValue());
        serverCommandSource.getServer().getPlayerManager().getIpBanList().add(bannedIpEntry);
        serverCommandSource.sendFeedback(() -> Text.translatable("commands.banip.success", ip, bannedIpEntry.getReason()), true);

        // feedback
        List<ServerPlayerEntity> list = serverCommandSource.getServer().getPlayerManager().getPlayersByIp(ip);
        if (!list.isEmpty()) {
            serverCommandSource.sendFeedback(() -> Text.translatable("commands.banip.info", list.size(), EntitySelector.getNames(list)), true);
        }
        for (ServerPlayerEntity serverPlayerEntity : list) {
            serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.ip_banned"));
        }

        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    @CommandNode("player")
    int player(@CommandSource CommandContext<ServerCommandSource> ctx, GameProfileCollection collection, String expiry, GreedyString reason) {
        MinecraftServer server = ctx.getSource().getServer();
        PlayerManager playerManager = server.getPlayerManager();
        Date expire = parseDate(expiry);

        for (GameProfile gameProfile : collection.getValue()) {
            // add
            BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, ctx.getSource().getName(), expire, reason.getValue());
            playerManager.getUserBanList().add(bannedPlayerEntry);
            ctx.getSource().sendFeedback(() -> Text.translatable("commands.ban.success", Text.literal(gameProfile.getName()), bannedPlayerEntry.getReason()), true);

            // kick
            ServerPlayerEntity serverPlayerEntity = playerManager.getPlayer(gameProfile.getId());
            if (serverPlayerEntity != null) {
                serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.banned"));
            }
        }

        return CommandHelper.Return.SUCCESS;
    }

    private static Date parseDate(String expiry) {

        // Regular expression to match hours, minutes, and seconds
        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(expiry);

        int totalSeconds = 0;

        // Parse the input string and convert to total seconds
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d":
                    totalSeconds += value * 86400;
                    break;
                case "h":
                    totalSeconds += value * 3600;
                    break;
                case "m":
                    totalSeconds += value * 60;
                    break;
                case "s":
                    totalSeconds += value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown time unit: " + unit);
            }
        }

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, totalSeconds);

        // Return the new date with added time
        return calendar.getTime();
    }

}
