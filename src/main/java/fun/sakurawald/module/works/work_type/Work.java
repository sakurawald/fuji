package fun.sakurawald.module.works.work_type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.works.WorksModule;
import fun.sakurawald.module.works.gui.ConfirmGui;
import fun.sakurawald.module.works.gui.InputSignGui;
import fun.sakurawald.util.MessageUtil;
import fun.sakurawald.util.TimeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fun.sakurawald.util.MessageUtil.ofString;

@Data
@Slf4j
public abstract class Work {

    public String type;
    public String id;
    public long createTimeMS;
    public String creator;
    public String name;
    public String introduction;
    public String level;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public String icon;

    public Work() {
        // for gson
    }

    public Work(ServerPlayer player, String name) {
        this.type = getType();
        this.id = generateID();
        this.createTimeMS = System.currentTimeMillis();
        this.creator = player.getGameProfile().getName();
        this.name = name;
        this.introduction = null;
        this.level = player.level().dimension().location().toString();
        this.x = player.position().x;
        this.y = player.position().y;
        this.z = player.position().z;
        this.yaw = player.getYRot();
        this.pitch = player.getXRot();
        this.icon = null;
    }

    private static Work getWorkByID(String uuid) {
        List<Work> works = ConfigManager.worksWrapper.instance().works;
        for (Work work : works) {
            if (work.getId().equals(uuid)) {
                return work;
            }
        }
        return null;
    }

    protected abstract String getType();

    @Override
    public boolean equals(Object o) {
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

    public abstract void openSpecializedSettingsGui(ServerPlayer player, SimpleGui parentGui);


    public void openGeneralSettingsGui(ServerPlayer player, SimpleGui parentGui) {
        Work work = this;
        final SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x1, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(net.minecraft.network.chat.Component.literal(ofString(player, "works.work.set.general_settings.title")));
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.NAME_TAG)
                .setName(net.minecraft.network.chat.Component.literal(ofString(player, "works.work.set.target.name")))
                .setCallback(() -> new InputSignGui(player, null) {
                    @Override
                    public void onClose() {
                        work.name = this.combineAllLines();
                        MessageUtil.sendMessage(player, "works.work.set.done", work.name);
                    }
                }.open())
        );
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.CHERRY_HANGING_SIGN)
                .setName(net.minecraft.network.chat.Component.literal(ofString(player, "works.work.set.target.introduction")))
                .setCallback(() -> new InputSignGui(player, null) {
                    @Override
                    public void onClose() {
                        work.introduction = this.combineAllLines();
                        MessageUtil.sendMessage(player, "works.work.set.done", work.introduction);
                    }
                }.open())
        );
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.END_PORTAL_FRAME)
                .setName(net.minecraft.network.chat.Component.literal(ofString(player, "works.work.set.target.position")))
                .setCallback(() -> {
                    work.level = player.serverLevel().dimension().location().toString();
                    work.x = player.position().x;
                    work.y = player.position().y;
                    work.z = player.position().z;
                    MessageUtil.sendMessage(player, "works.work.set.done", "(%s, %f, %f, %f)".formatted(work.level, work.x, work.y, work.z));
                    gui.close();
                })
        );
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.PAINTING)
                .setName(net.minecraft.network.chat.Component.literal(ofString(player, "works.work.set.target.icon")))
                .setCallback(() -> {
                    ItemStack mainHandItem = player.getMainHandItem();
                    if (mainHandItem.isEmpty()) {
                        MessageUtil.sendActionBar(player, "works.work.set.target.icon.no_item");
                        gui.close();
                        return;
                    }
                    work.icon = BuiltInRegistries.ITEM.getKey(mainHandItem.getItem()).toString();
                    MessageUtil.sendMessage(player, "works.work.set.done", work.icon);
                    gui.close();
                })
        );

        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.BARRIER)
                .setName(net.minecraft.network.chat.Component.literal(ofString(player, "works.work.set.target.delete")))
                .setCallback(() -> new ConfirmGui(player) {
                    @Override
                    public void onConfirm() {
                        ConfigManager.worksWrapper.instance().works.remove(work);
                        MessageUtil.sendActionBar(player, "works.work.delete.done");
                    }
                }.open())

        );

        gui.setSlot(8, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setSkullOwner(WorksModule.PREVIOUS_PAGE_ICON)
                .setName(net.minecraft.network.chat.Component.literal(ofString(player, "works.list.back")))
                .setCallback(parentGui::open)
        );

        // let's open it now
        gui.open();
    }

    public Item asItem() {
        CompoundTag rootTag = new CompoundTag();
        rootTag.putString("id", this.icon == null ? getDefaultIcon() : this.icon);
        rootTag.putInt("Count", 1);
        return ItemStack.of(rootTag).getItem();
    }

    public List<Component> asLore(ServerPlayer player) {
        ArrayList<Component> ret = new ArrayList<>();
        ret.add(Component.literal("%s: %s".formatted(ofString(player, "works.work.prop.creator"), this.creator)).withStyle(ChatFormatting.GOLD));
        ret.add(Component.literal("%s: %s".formatted(ofString(player, "works.work.prop.time"), TimeUtil.getFormattedDate(this.createTimeMS))).withStyle(ChatFormatting.GRAY));
        ret.add(Component.literal("%s: %s".formatted(ofString(player, "works.work.prop.id"), this.id)).withStyle(ChatFormatting.GRAY));
        ret.add(Component.literal("%s: %s".formatted(ofString(player, "works.work.prop.dimension"), this.level)).withStyle(ChatFormatting.GRAY));
        ret.add(Component.literal("%s: %.3f %.3f %.3f".formatted(ofString(player, "works.work.prop.coordinate"), this.x, this.y, this.z)).withStyle(ChatFormatting.GRAY));
        if (this.introduction != null)
            ret.add(Component.literal("%s: ".formatted(ofString(player, "works.work.prop.introduction")) + this.introduction).withStyle(ChatFormatting.GOLD));
        return ret;
    }

    private String generateID() {
        String ret = null;
        while (ret == null || getWorkByID(ret) != null) {
            ret = UUID.randomUUID().toString().substring(0, 8);
        }
        return ret;
    }


    public static class WorkTypeAdapter implements JsonDeserializer<Work> {
        @Override
        public Work deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String type = json.getAsJsonObject().get("type").getAsString();
            if (type.equals(WorkType.NonProductionWork.name()))
                return context.deserialize(json, NonProductionWork.class);
            if (type.equals(WorkType.ProductionWork.name())) return context.deserialize(json, ProductionWork.class);
            return null;
        }

        public enum WorkType {NonProductionWork, ProductionWork}
    }
}


