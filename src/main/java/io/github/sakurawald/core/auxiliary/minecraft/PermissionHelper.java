package io.github.sakurawald.core.auxiliary.minecraft;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@UtilityClass
public class PermissionHelper {

    private static LuckPerms luckPerms;

    private static @Nullable LuckPerms getAPI() {
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

    /*
     * If you loadUser() for a fake-player spawned by carpet-fabric, then the User data will be loaded into the memory by luckperms.
     * Luckperms will assign the group 'default' for the fake-player, but will never save the User data back to storage.
     * And also, if you issue `/lp user fake_player permission info`, luckperms will say there is no User data for this player.
     */
    private static User loadUser(@NonNull LuckPerms api, UUID uuid) {
        UserManager userManager = api.getUserManager();

        // cache
        if (userManager.isLoaded(uuid)) {
            return userManager.getUser(uuid);
        }

        CompletableFuture<User> userFuture = userManager.loadUser(uuid);
        return userFuture.join();
    }

    public static @NotNull Tristate checkPermission(@NotNull UUID uuid, @Nullable String permission) {
        if (permission == null) return Tristate.FALSE;
        if (permission.isBlank()) return Tristate.FALSE;

        LuckPerms api = getAPI();
        if (api == null) {
            return Tristate.UNDEFINED;
        }

        User user = loadUser(api, uuid);
        return user
            .getCachedData()
            .getPermissionData().checkPermission(permission);
    }

    public static boolean hasPermission(UUID uuid, @Nullable String permission) {
        return checkPermission(uuid, permission).asBoolean();
    }

    public static <T> @NonNull Optional<T> getMeta(@NotNull UUID uuid, @Nullable String meta, @NonNull Function<String, ? extends T> valueTransformer) {
        if (meta == null) return Optional.empty();

        LuckPerms api = getAPI();
        if (api == null) {
            return Optional.empty();
        }

        User user = loadUser(api, uuid);
        return user.getCachedData()
            .getMetaData()
            .getMetaValue(meta, valueTransformer);
    }

    public static @Nullable String getPrefix(UUID uuid) {
        LuckPerms api = getAPI();
        if (api == null) {
            return null;
        }

        User user = loadUser(api, uuid);

        return user
            .getCachedData()
            .getMetaData()
            .getPrefix();

    }

    public static @Nullable String getSuffix(UUID uuid) {
        LuckPerms api = getAPI();
        if (api == null) {
            return null;
        }

        User user = loadUser(api, uuid);

        return user
            .getCachedData()
            .getMetaData()
            .getSuffix();

    }

    private static void ensureApiNotNull(LuckPerms api) {
        if (api == null) {
            throw new RuntimeException("Luckperms api is null now !");
        }
    }

    @SuppressWarnings("unused")
    public static void setPermission(UUID uuid, @NotNull String string) {
        LuckPerms api = getAPI();
        ensureApiNotNull(api);

        User user = loadUser(api, uuid);
        PermissionNode.Builder builder = PermissionNode.builder();

        builder.permission(string);

        PermissionNode node = builder.build();
        user.data().add(node);
        getAPI().getUserManager().saveUser(user);
    }

    @SuppressWarnings("unused")
    public static void unsetPermission(UUID uuid, @NotNull String string) {
        LuckPerms api = getAPI();
        ensureApiNotNull(api);

        User user = loadUser(api, uuid);
        user.data().clear(NodeType.PERMISSION.predicate(p -> p.getPermission().equals(string)));
        getAPI().getUserManager().saveUser(user);
    }

    @SuppressWarnings("unused")
    public static void setMeta(UUID uuid, @NotNull String key, @NotNull String value) {
        LuckPerms api = getAPI();
        ensureApiNotNull(api);

        User user = loadUser(api, uuid);

        MetaNode node = MetaNode.builder(key, value).build();
        user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(key)));
        user.data().add(node);
        getAPI().getUserManager().saveUser(user);
    }

}
