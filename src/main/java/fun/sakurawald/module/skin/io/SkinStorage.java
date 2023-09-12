package fun.sakurawald.module.skin.io;

import com.mojang.authlib.properties.Property;
import fun.sakurawald.config.ConfigManager;
import lombok.Getter;

import java.util.*;

public class SkinStorage {

    public static final Property DEFAULT_SKIN = new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0",
            "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC");

    private final Map<UUID, Property> skinMap = new HashMap<>();

    @Getter
    private final SkinIO skinIO;

    public SkinStorage(SkinIO skinIO) {
        this.skinIO = skinIO;
    }

    public Property getRandomDefaultSkin(UUID uuid) {
        if (!skinMap.containsKey(uuid)) {
            ArrayList<Property> defaultSkins = ConfigManager.configWrapper.instance().modules.skin.default_skins;
            Property skin = defaultSkins.get(new Random().nextInt(defaultSkins.size()));
            setSkin(uuid, skin);
        }

        return skinMap.get(uuid);
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

    public void setSkin(UUID uuid, Property skin) {
        if (skin == null)
            skin = DEFAULT_SKIN;

        skinMap.put(uuid, skin);
    }
}
