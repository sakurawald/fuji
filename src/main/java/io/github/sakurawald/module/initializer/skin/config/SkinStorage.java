package io.github.sakurawald.module.initializer.skin.config;

import com.mojang.authlib.properties.Property;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.skin.SkinInitializer;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SkinStorage {

    private final SkinInitializer initializer = Managers.getModuleManager().getInitializer(SkinInitializer.class);
    private final Map<UUID, Property> skinMap = new HashMap<>();

    @Getter
    private final SkinIO skinIO;

    public SkinStorage(SkinIO skinIO) {
        this.skinIO = skinIO;
    }

    public Property getRandomSkin(UUID uuid) {
        if (!skinMap.containsKey(uuid)) {
            List<Property> defaultSkins = initializer.data.getModel().random_skins;
            Property skin = defaultSkins.get(new Random().nextInt(defaultSkins.size()));
            setSkin(uuid, skin);
        }

        return skinMap.get(uuid);
    }

    public Property getDefaultSkin() {
        return initializer.data.getModel().default_skin;
    }

    public Property getSkin(UUID uuid) {
        if (!skinMap.containsKey(uuid)) {
            Property skin = skinIO.loadSkin(uuid);
            setSkin(uuid, skin);
        }

        return skinMap.get(uuid);
    }

    public void removeSkin(UUID uuid) {
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
