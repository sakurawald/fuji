package io.github.sakurawald.module.initializer.skin;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.sakurawald.core.annotation.Cite;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.module.initializer.skin.config.SkinIO;
import io.github.sakurawald.module.initializer.skin.config.SkinStorage;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Cite("https://github.com/Suiranoil/SkinRestorer")
public class SkinRestorer {

    @Getter
    private static final SkinStorage skinStorage = new SkinStorage(new SkinIO(ReflectionUtil.getModuleConfigPath(SkinInitializer.class).resolve("skin-data")));

    public static CompletableFuture<Pair<Collection<ServerPlayerEntity>, Collection<GameProfile>>> setSkinAsync(@NotNull MinecraftServer server, @NotNull Collection<GameProfile> targets, @NotNull Supplier<Property> skinSupplier) {
        return CompletableFuture.<Pair<Property, Collection<GameProfile>>>supplyAsync(() -> {
            Set<GameProfile> acceptedProfiles = new HashSet<>();
            Property skin = skinSupplier.get();

            LogUtil.debug("skinSupplier.get() -> skin = {}", skin);
            if (skin == null) {
                LogUtil.error("cannot get the skin for {}", targets.stream().findFirst().orElseThrow());
                return Pair.of(null, Collections.emptySet());
            }

            for (GameProfile profile : targets) {
                SkinRestorer.getSkinStorage().setSkin(profile.getId(), skin);
                acceptedProfiles.add(profile);
            }

            return Pair.of(skin, acceptedProfiles);
        }).<Pair<Collection<ServerPlayerEntity>, Collection<GameProfile>>>thenApplyAsync(pair -> {
            Property skin = pair.left();
            if (skin == null)
                return Pair.of(Collections.emptySet(), Collections.emptySet());

            Collection<GameProfile> acceptedProfiles = pair.right();
            Set<ServerPlayerEntity> acceptedPlayers = new HashSet<>();
            JsonObject newSkinJson = BaseConfigurationHandler.getGson().fromJson(new String(Base64.getDecoder().decode(skin.value()), StandardCharsets.UTF_8), JsonObject.class);
            newSkinJson.remove("timestamp");
            for (GameProfile profile : acceptedProfiles) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(profile.getId());

                if (player == null || arePropertiesEquals(newSkinJson, player.getGameProfile()))
                    continue;

                applyRestoredSkin(player, skin);
                for (PlayerEntity observer : player.getWorld().getPlayers()) {
                    ServerPlayerEntity observer1 = (ServerPlayerEntity) observer;
                    observer1.networkHandler.sendPacket(new PlayerRemoveS2CPacket(Collections.singletonList(player.getUuid())));
                    observer1.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player)); // refresh the player information
                    if (player != observer1 && observer1.canSee(player)) {
                        observer1.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(player.getId()));
                        observer1.networkHandler.sendPacket(new EntitySpawnS2CPacket(player, 0, player.getBlockPos()));
                        observer1.networkHandler.sendPacket(new EntityPositionS2CPacket(player));
                        observer1.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(player.getId(), player.getDataTracker().getChangedEntries()));
                    } else if (player == observer1) {
                        observer1.networkHandler.sendPacket(new PlayerRespawnS2CPacket(player.createCommonPlayerSpawnInfo(player.getServerWorld()), (byte) 2));
                        observer1.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(observer1.getInventory().selectedSlot));
                        observer1.sendAbilitiesUpdate();
                        observer1.playerScreenHandler.updateToClient();
                        for (StatusEffectInstance instance : observer1.getStatusEffects()) {
                            // original code: observer1.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(observer1.getId(), instance));
                            observer1.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(observer1.getId(), instance, false));
                        }
                        observer1.networkHandler.requestTeleport(observer1.getX(), observer1.getY(), observer1.getZ(), observer1.getYaw(), observer1.getPitch());
                        observer1.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(player.getId(), player.getDataTracker().getChangedEntries()));
                        observer1.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                    }
                }
                acceptedPlayers.add(player);
            }
            return Pair.of(acceptedPlayers, acceptedProfiles);
        }, server).orTimeout(10, TimeUnit.SECONDS).exceptionally(e -> Pair.of(Collections.emptySet(), Collections.emptySet()));
    }

    private static void applyRestoredSkin(@NotNull ServerPlayerEntity playerEntity, Property skin) {
        playerEntity.getGameProfile().getProperties().removeAll("textures");
        playerEntity.getGameProfile().getProperties().put("textures", skin);
    }

    private static boolean arePropertiesEquals(@NotNull JsonObject x, @NotNull GameProfile y) {
        Property py = y.getProperties().get("textures").stream().findFirst().orElse(null);
        if (py == null)
            return false;

        try {
            JsonObject jy = BaseConfigurationHandler.getGson().fromJson(new String(Base64.getDecoder().decode(py.value()), StandardCharsets.UTF_8), JsonObject.class);
            jy.remove("timestamp");
            return x.equals(jy);
        } catch (Exception ex) {
            LogUtil.info("can not compare skin", ex);
            return false;
        }
    }

}
