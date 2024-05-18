package io.github.sakurawald.module.initializer.head.api;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

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
            ret.setCustomName(Text.literal(name).styled(style -> style.withItalic(false)));
        }

        if (tags != null) {
            NbtCompound displayTag = ret.getOrCreateSubNbt("display");
            NbtList loreTag = new NbtList();
            loreTag.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(tags))));
            displayTag.put("Lore", loreTag);
        }

        NbtCompound ownerTag = ret.getOrCreateSubNbt("SkullOwner");
        ownerTag.putUuid("Id", uuid);

        NbtCompound propertiesTag = new NbtCompound();
        NbtList texturesTag = new NbtList();

        NbtCompound textureValue = new NbtCompound();
        textureValue.putString("Value", value);
        texturesTag.add(textureValue);

        propertiesTag.put("textures", texturesTag);
        ownerTag.put("Properties", propertiesTag);

        return ret;
    }
}
