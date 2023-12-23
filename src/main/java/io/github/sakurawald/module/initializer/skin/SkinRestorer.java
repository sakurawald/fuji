package io.github.sakurawald.module.initializer.skin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.skin.io.SkinIO;
import io.github.sakurawald.module.initializer.skin.io.SkinStorage;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.BiomeManager;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// Thanks to: https://modrinth.com/mod/skinrestorer
@Slf4j
public class SkinRestorer {

    private static final Gson gson = new Gson();
    @Getter
    private static final SkinStorage skinStorage = new SkinStorage(new SkinIO(Fuji.CONFIG_PATH.resolve("skin")));


    public static CompletableFuture<Pair<Collection<ServerPlayer>, Collection<GameProfile>>> setSkinAsync(MinecraftServer server, Collection<GameProfile> targets, Supplier<Property> skinSupplier) {
        return CompletableFuture.<Pair<Property, Collection<GameProfile>>>supplyAsync(() -> {
            HashSet<GameProfile> acceptedProfiles = new HashSet<>();
            Property skin = skinSupplier.get();
            if (skin == null) {
                log.error("Cannot get the skin for {}", targets.stream().findFirst().orElseThrow());
                return Pair.of(null, Collections.emptySet());
            }

            for (GameProfile profile : targets) {
                SkinRestorer.getSkinStorage().setSkin(profile.getId(), skin);
                acceptedProfiles.add(profile);
            }

            return Pair.of(skin, acceptedProfiles);
        }).<Pair<Collection<ServerPlayer>, Collection<GameProfile>>>thenApplyAsync(pair -> {
            Property skin = pair.left();
            if (skin == null)
                return Pair.of(Collections.emptySet(), Collections.emptySet());

            Collection<GameProfile> acceptedProfiles = pair.right();
            HashSet<ServerPlayer> acceptedPlayers = new HashSet<>();
            JsonObject newSkinJson = gson.fromJson(new String(Base64.getDecoder().decode(skin.getValue()), StandardCharsets.UTF_8), JsonObject.class);
            newSkinJson.remove("timestamp");
            for (GameProfile profile : acceptedProfiles) {
                ServerPlayer player = server.getPlayerList().getPlayer(profile.getId());

                if (player == null || arePropertiesEquals(newSkinJson, player.getGameProfile()))
                    continue;

                applyRestoredSkin(player, skin);
                for (Player observer : player.level().players()) {
                    ServerPlayer observer1 = (ServerPlayer) observer;
                    observer1.connection.send(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(player.getUUID())));
                    observer1.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, player)); // refresh the player information
                    if (player != observer1 && observer1.hasLineOfSight(player)) {
                        observer1.connection.send(new ClientboundRemoveEntitiesPacket(player.getId()));
                        observer1.connection.send(new ClientboundAddPlayerPacket(player));
                        observer1.connection.send(new ClientboundTeleportEntityPacket(player));
                        observer1.connection.send(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData().getNonDefaultValues()));
                    } else if (player == observer1) {
                        observer1.connection.send(new ClientboundRespawnPacket(
                                observer1.level().dimensionTypeId(),
                                observer1.level().dimension(),
                                BiomeManager.obfuscateSeed(observer1.serverLevel().getSeed()),
                                observer1.gameMode.getGameModeForPlayer(),
                                observer1.gameMode.getPreviousGameModeForPlayer(),
                                observer1.level().isDebug(),
                                observer1.serverLevel().isFlat(),
                                (byte) 2,
                                Optional.empty(),
                                observer1.getPortalCooldown()
                        ));
                        observer1.connection.send(new ClientboundSetCarriedItemPacket(observer1.getInventory().selected));
                        observer1.onUpdateAbilities();
                        observer1.inventoryMenu.broadcastFullState();
                        for (MobEffectInstance instance : observer1.getActiveEffects()) {
                            observer1.connection.send(new ClientboundUpdateMobEffectPacket(observer1.getId(), instance));
                        }
                        observer1.connection.teleport(observer1.getX(), observer1.getY(), observer1.getZ(), observer1.getYRot(), observer1.getXRot());
                        observer1.connection.send(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData().getNonDefaultValues()));
                        observer1.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                    }
                }
                acceptedPlayers.add(player);
            }
            return Pair.of(acceptedPlayers, acceptedProfiles);
        }, server).orTimeout(10, TimeUnit.SECONDS).exceptionally(e -> Pair.of(Collections.emptySet(), Collections.emptySet()));
    }

    private static void applyRestoredSkin(ServerPlayer playerEntity, Property skin) {
        playerEntity.getGameProfile().getProperties().removeAll("textures");
        playerEntity.getGameProfile().getProperties().put("textures", skin);
    }

    private static boolean arePropertiesEquals(@NotNull JsonObject x, @NotNull GameProfile y) {
        Property py = y.getProperties().get("textures").stream().findFirst().orElse(null);
        if (py == null)
            return false;

        try {
            JsonObject jy = gson.fromJson(new String(Base64.getDecoder().decode(py.getValue()), StandardCharsets.UTF_8), JsonObject.class);
            jy.remove("timestamp");
            return x.equals(jy);
        } catch (Exception ex) {
            log.info("Can not compare skin", ex);
            return false;
        }
    }

}