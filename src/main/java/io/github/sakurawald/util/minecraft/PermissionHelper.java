package io.github.sakurawald.util.minecraft;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.util.Tristate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
@UtilityClass
public class PermissionHelper {

    private static LuckPerms luckPerms;

    private static LuckPerms getAPI() {
        if (luckPerms == null) {
            try {
                luckPerms = LuckPermsProvider.get();
            } catch (Exception e) {
                return null;
            }
            return luckPerms;
        }
        return luckPerms;
    }

    public static Tristate checkPermission(ServerPlayerEntity player, String permission) {
        LuckPerms api = getAPI();
        if (api == null) {
            return Tristate.UNDEFINED;
        }

        User user = loadUser(api, player);
        return user
                .getCachedData()
                .getPermissionData().checkPermission(permission);
    }

    public static boolean hasPermission(PlayerEntity player, String permission) {
        return checkPermission((ServerPlayerEntity) player, permission).asBoolean();
    }

    public static boolean hasPermission(ServerPlayerEntity player, String permission) {
        return checkPermission(player, permission).asBoolean();
    }

    private static User loadUser(@NonNull LuckPerms api, ServerPlayerEntity player) {
        User user;
        if (EntityHelper.isNonRealPlayer(player)) {
            UserManager userManager = api.getUserManager();
            CompletableFuture<User> userFuture = userManager.loadUser(player.getUuid());
            user = userFuture.join();
            userManager.savePlayerData(player.getUuid(), player.getGameProfile().getName());
        } else {
            user = api.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);
        }

        return user;
    }

    public static <T> @NonNull Optional<T> getMeta(ServerPlayerEntity player, String meta, @NonNull Function<String, ? extends T> valueTransformer) {
        LuckPerms api = getAPI();
        if (api == null) {
            return Optional.empty();
        }

        User user = loadUser(api, player);
        return user.getCachedData()
                .getMetaData()
                .getMetaValue(meta, valueTransformer);

    }

    public static String getPrefix(ServerPlayerEntity player) {
        LuckPerms api = getAPI();
        if (api == null) {
            return null;
        }

        User user = loadUser(api, player);

        return user
                .getCachedData()
                .getMetaData()
                .getPrefix();

    }

    public static <T> String getSuffix(ServerPlayerEntity player) {
        LuckPerms api = getAPI();
        if (api == null) {
            return null;
        }

        User user = loadUser(api, player);

        return user
                .getCachedData()
                .getMetaData()
                .getSuffix();

    }

    public static void saveMeta(ServerPlayerEntity player, String key, String value) {
        LuckPerms api = getAPI();
        if (api == null) {
            throw new RuntimeException("Failed to save meta");
        }

        User user = loadUser(api, player);

        MetaNode node = MetaNode.builder(key, value).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(key)));
        user.data().add(node);
        getAPI().getUserManager().saveUser(user);
    }

}
