package io.github.sakurawald.module.initializer.head.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Head {
    public final String name;
    public final UUID uuid;
    public final String value;
    @Nullable
    public final String tags;

    public Head(@Nullable String name, UUID uuid, String value, @Nullable String tags) {
        this.name = name;
        this.uuid = uuid;
        this.value = value;
        this.tags = tags;
    }

    public Head(UUID uuid, String value) {
        this.name = "";
        this.uuid = uuid;
        this.value = value;
        this.tags = null;
    }

    public String getTagsOrEmpty() {
        return tags == null ? "" : tags;
    }

    public ItemStack of() {
        ItemStack ret = new ItemStack(Items.PLAYER_HEAD);
        if (name != null) {
            ret.setHoverName(Component.literal(name).withStyle(style -> style.withItalic(false)));
        }

        if (tags != null) {
            CompoundTag displayTag = ret.getOrCreateTagElement("display");
            ListTag loreTag = new ListTag();
            loreTag.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(tags))));
            displayTag.put("Lore", loreTag);
        }

        CompoundTag ownerTag = ret.getOrCreateTagElement("SkullOwner");
        ownerTag.putUUID("Id", uuid);

        CompoundTag propertiesTag = new CompoundTag();
        ListTag texturesTag = new ListTag();

        CompoundTag textureValue = new CompoundTag();
        textureValue.putString("Value", value);
        texturesTag.add(textureValue);

        propertiesTag.put("textures", texturesTag);
        ownerTag.put("Properties", propertiesTag);

        return ret;
    }
}
