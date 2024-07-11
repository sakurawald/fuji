package io.github.sakurawald.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class LuckPermsUtil {

    public static <T> Optional<? extends T> getMetaValue(String player, String key, @NonNull Function<String, ? extends T> transformer) {
        LuckPerms api = getAPI();
        return api
                .getUserManager()
                .getUser(player)
                .getCachedData()
                .getMetaData()
                .getMetaValue(key, transformer);
    }

    public static <T> Optional<? extends T> getMetaValue(ServerPlayerEntity player, String key, @NonNull Function<String, ? extends T> transformer) {
        return getMetaValue(player.getGameProfile().getName(), key, transformer);
    }

    public static LuckPerms getAPI() {
        return LuckPermsProvider.get();
    }



}
