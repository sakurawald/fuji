package io.github.sakurawald.module.initializer.head.api;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.RegistryUtil;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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

    public Head(String name, UUID uuid, String value) {
        this(name, uuid, value, null);
    }

    public Head(UUID uuid, String value) {
        this("", uuid, value, null);
    }

    public String getTagsOrEmpty() {
        return tags == null ? "" : tags;
    }

    public ItemStack of() {
        return new GuiElementBuilder().setItem(Items.PLAYER_HEAD).setName(Text.literal(name)).setSkullOwner(value, null, uuid).asStack();
    }
}
