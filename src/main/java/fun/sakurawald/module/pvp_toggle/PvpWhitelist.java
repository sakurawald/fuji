package fun.sakurawald.module.pvp_toggle;

import com.mojang.authlib.GameProfile;
import fun.sakurawald.ModMain;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

import java.io.File;

public class PvpWhitelist {
    private static UserWhiteList pvpWhitelist;

    public static void create(File file) {
        pvpWhitelist = new UserWhiteList(file);
        load();
    }

    public static void load() {
        try {
            pvpWhitelist.load();
        } catch (Exception error) {
            ModMain.LOGGER.error("Failed to load pvp whitelist: ", error);
        }
    }

    public static boolean contains(GameProfile player) {
        return pvpWhitelist.isWhiteListed(player);
    }

    public static void addPlayer(GameProfile player) {
        pvpWhitelist.add(new UserWhiteListEntry(player));
    }

    public static void removePlayer(GameProfile player) {
        pvpWhitelist.remove(player);
    }

    public static String[] getPlayers() {
        return pvpWhitelist.getUserList();
    }
}
