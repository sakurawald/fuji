package io.github.sakurawald.module.initializer.skin.structure;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.sakurawald.core.auxiliary.RandomUtil;
import io.github.sakurawald.module.initializer.skin.SkinInitializer;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SkinStorage {

    private final Map<UUID, Property> skinMap = new HashMap<>();

    @Getter
    private final SkinIO skinIO;

    public SkinStorage(SkinIO skinIO) {
        this.skinIO = skinIO;
    }

    public Property getDefaultSkin() {
        return RandomUtil.drawList(SkinInitializer.config.model().default_skins);
    }

    public boolean isDefaultSkin(GameProfile gameProfile) {
        Property textures = gameProfile.getProperties().get("textures").stream().findFirst().orElse(null);
        if (textures == null) return false;

        return SkinInitializer.config.model().default_skins.stream().anyMatch(it -> it.value().equals(textures.value()));
    }

    public Property getSkin(UUID uuid) {
        if (!skinMap.containsKey(uuid)) {
            Property skin = skinIO.loadSkin(uuid);
            setSkin(uuid, skin);
        }

        return skinMap.get(uuid);
    }

    public void saveSkin(UUID uuid) {
        if (skinMap.containsKey(uuid)) {
            skinIO.saveSkin(uuid, skinMap.get(uuid));
        }
    }

    public void setSkin(UUID uuid, @Nullable Property skin) {
        // if a player has no skin, use default skin.
        if (skin == null)
            skin = this.getDefaultSkin();

        skinMap.put(uuid, skin);
    }
}
