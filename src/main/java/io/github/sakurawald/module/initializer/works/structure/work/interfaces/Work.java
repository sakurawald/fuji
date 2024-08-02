package io.github.sakurawald.module.initializer.works.structure.work.interfaces;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.common.gui.ConfirmGui;
import io.github.sakurawald.module.common.gui.InputSignGui;
import io.github.sakurawald.module.initializer.works.structure.work.impl.NonProductionWork;
import io.github.sakurawald.module.initializer.works.structure.work.impl.ProductionWork;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.minecraft.GuiHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.Data;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data

public abstract class Work {

    public String type;
    public String id;
    public long createTimeMS;
    public String creator;
    public String name;
    public @Nullable String introduction;
    public String level;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public @Nullable String icon;

    @SuppressWarnings("unused")
    public Work() {
        // for gson
    }

    public Work(@NotNull ServerPlayerEntity player, String name) {
        this.type = getType();
        this.id = generateID();
        this.createTimeMS = System.currentTimeMillis();
        this.creator = player.getGameProfile().getName();
        this.name = name;
        this.introduction = null;
        this.level = player.getWorld().getRegistryKey().getValue().toString();
        this.x = player.getPos().x;
        this.y = player.getPos().y;
        this.z = player.getPos().z;
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
        this.icon = null;
    }

    private static @Nullable Work getWorkByID(String uuid) {
        List<Work> works = WorksInitializer.worksHandler.model().works;
        for (Work work : works) {
            if (work.getId().equals(uuid)) {
                return work;
            }
        }
        return null;
    }

    protected abstract String getType();

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Work work = (Work) o;
        return id.equals(work.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    protected abstract String getDefaultIcon();

    public abstract void openSpecializedSettingsGui(ServerPlayerEntity player, SimpleGui parentGui);


    public void openGeneralSettingsGui(@NotNull ServerPlayerEntity player, @NotNull SimpleGui parentGui) {
        Work work = this;
        final SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(MessageHelper.ofText(player, "works.work.set.general_settings.title"));
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.NAME_TAG)
                .setName(MessageHelper.ofText(player, "works.work.set.target.name"))
                .setCallback(() -> new InputSignGui(player, null) {
                    @Override
                    public void onClose() {
                        String newValue = this.reduceInput();
                        if (newValue == null) {
                            MessageHelper.sendActionBar(player, "works.work.add.empty_name");
                            return;
                        }
                        work.name = newValue;
                        MessageHelper.sendMessage(player, "works.work.set.done", work.name);
                    }
                }.open())
        );
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.CHERRY_HANGING_SIGN)
                .setName(MessageHelper.ofText(player, "works.work.set.target.introduction"))
                .setCallback(() -> new InputSignGui(player, null) {
                    @Override
                    public void onClose() {
                        work.introduction = this.reduceInput();
                        MessageHelper.sendMessage(player, "works.work.set.done", work.introduction);
                    }
                }.open())
        );
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.END_PORTAL_FRAME)
                .setName(MessageHelper.ofText(player, "works.work.set.target.position"))
                .setCallback(() -> {
                    work.level = player.getServerWorld().getRegistryKey().getValue().toString();
                    work.x = player.getPos().x;
                    work.y = player.getPos().y;
                    work.z = player.getPos().z;
                    MessageHelper.sendMessage(player, "works.work.set.done", "(%s, %f, %f, %f)".formatted(work.level, work.x, work.y, work.z));
                    gui.close();
                })
        );
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.PAINTING)
                .setName(MessageHelper.ofText(player, "works.work.set.target.icon"))
                .setCallback(() -> {
                    ItemStack mainHandItem = player.getMainHandStack();
                    if (mainHandItem.isEmpty()) {
                        MessageHelper.sendActionBar(player, "works.work.set.target.icon.no_item");
                        gui.close();
                        return;
                    }
                    work.icon = Registries.ITEM.getId(mainHandItem.getItem()).toString();
                    MessageHelper.sendMessage(player, "works.work.set.done", work.icon);
                    gui.close();
                })
        );

        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.BARRIER)
                .setName(MessageHelper.ofText(player, "works.work.set.target.delete"))
                .setCallback(() -> new ConfirmGui(player) {
                    @Override
                    public void onConfirm() {
                        WorksInitializer.worksHandler.model().works.remove(work);
                        MessageHelper.sendActionBar(player, "works.work.delete.done");
                    }
                }.open())

        );

        gui.setSlot(8, GuiHelper.makePreviousPageButton(player)
                .setCallback(parentGui::open)
        );

        // let's open it now
        gui.open();
    }

    public Item asItem() {
        NbtCompound rootTag = new NbtCompound();
        rootTag.putString("id", this.getIcon());
        rootTag.putInt("Count", 1);

        return ItemStack.fromNbt(ServerHelper.getDefaultServer().getRegistryManager(),rootTag).get().getItem();
    }

    public @NotNull String getIcon() {
        return this.icon == null ? getDefaultIcon() : this.icon;
    }

    public List<Text> asLore(ServerPlayerEntity player) {
        ArrayList<Text> ret = new ArrayList<>();
        ret.add(MessageHelper.ofText(player, "works.work.prop.creator", this.creator));
        if (this.introduction != null)
            ret.add(MessageHelper.ofText(player, "works.work.prop.introduction", this.introduction));
        ret.add(MessageHelper.ofText(player, "works.work.prop.time", DateUtil.toStandardDateFormat(this.createTimeMS)));
        ret.add(MessageHelper.ofText(player, "works.work.prop.dimension", this.level));
        ret.add(MessageHelper.ofText(player, "works.work.prop.coordinate", this.x, this.y, this.z));
        return ret;
    }

    private @NotNull String generateID() {
        String ret = null;
        while (ret == null || getWorkByID(ret) != null) {
            ret = UUID.randomUUID().toString().substring(0, 8);
        }
        return ret;
    }


    public static class WorkTypeAdapter implements JsonDeserializer<Work> {
        @Override
        public @Nullable Work deserialize(@NotNull JsonElement json, Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
            String type = json.getAsJsonObject().get("type").getAsString();
            if (type.equals(WorkType.NonProductionWork.name()))
                return context.deserialize(json, NonProductionWork.class);
            if (type.equals(WorkType.ProductionWork.name())) return context.deserialize(json, ProductionWork.class);
            return null;
        }

        public enum WorkType {NonProductionWork, ProductionWork}
    }
}


