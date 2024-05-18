package io.github.sakurawald.module.initializer.head.api;

import com.mojang.serialization.MapDecoder;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.RegistryUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtHelper;
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

    public Head(String name, UUID uuid, String value) {
        this(name, uuid, value,null);
    }

    public Head(UUID uuid, String value) {
        this("", uuid, value, null);
    }

    public String getTagsOrEmpty() {
        return tags == null ? "" : tags;
    }

    public ItemStack of() {
        ItemStack ret = new ItemStack(Items.PLAYER_HEAD);
        if (name != null) {
            ret.set(DataComponentTypes.CUSTOM_NAME,Text.literal(name).styled(style -> style.withItalic(false)));
        }

        if (tags != null) {
            NbtCompound displayTag = new NbtCompound();
            NbtList loreTag = new NbtList();
            loreTag.add(NbtString.of(Text.Serialization.toJsonString(Text.literal(tags), RegistryUtil.getDefaultWrapperLookup())));

            displayTag.put("Lore", loreTag);
            ret.apply(DataComponentTypes.CUSTOM_DATA,NbtComponent.DEFAULT, comp -> comp.apply(cur -> {
                cur.put("display", displayTag);
            }));
        }



        NbtCompound ownerTag = new NbtCompound();
        ownerTag.putUuid("Id", uuid);

        NbtCompound propertiesTag = new NbtCompound();
        NbtList texturesTag = new NbtList();

        NbtCompound textureValue = new NbtCompound();
        textureValue.putString("Value", value);
        texturesTag.add(textureValue);

        propertiesTag.put("textures", texturesTag);
        ownerTag.put("Properties", propertiesTag);

        ret.apply(DataComponentTypes.CUSTOM_DATA,NbtComponent.DEFAULT, comp -> comp.apply(cur -> {
            cur.put("SkullOwner", ownerTag);
        }));

        return ret;
    }
}
