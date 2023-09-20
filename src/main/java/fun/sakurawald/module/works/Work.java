package fun.sakurawald.module.works;

import com.google.gson.JsonObject;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.JsonUtils;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Data
@ToString
@Slf4j
public class Work {
    public static final String DEFAULT_ICON = "minecraft:nether_star";
    String id;
    long createTimeMS;
    String creator;
    String name;
    String introduction;
    String residence;
    String icon;

    int sampleTimeMS;
    HashMap<String, Long> counter;

    public Work(String creator, String name) {
        this.id = generateID();
        this.createTimeMS = System.currentTimeMillis();
        this.creator = creator;
        this.name = name;
        this.introduction = null;
        this.residence = null;
    }

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

    private List<BlockPos> resolveHoppers(ServerPlayer player) {

//        player.serverLevel().getChunkAt().block


        return new ArrayList<>();
    }


    private void startSampling() {

    }

    private void stopSampling() {

    }

    public ItemStack asItemStack() {
        CompoundTag rootTag = new CompoundTag();
        rootTag.putString("id", this.icon == null ? DEFAULT_ICON : this.icon);
        rootTag.putInt("Count", 1);
        ItemStack of = ItemStack.of(rootTag);

        CompoundTag tagTag = new CompoundTag();
        of.setTag(tagTag);

        CompoundTag displayTag = new CompoundTag();
        tagTag.put("display", displayTag);

        // set display name
        displayTag.putString("Name", ofNameJson(this.name));

        // set lore
        ListTag loreTag = new ListTag();
        loreTag.add(StringTag.valueOf(ofLoreJson("Creator: " + this.creator)));
        loreTag.add(StringTag.valueOf(ofLoreJson("Time: " + this.createTimeMS)));
        loreTag.add(StringTag.valueOf(ofLoreJson("ID: " + this.id)));
        if (this.residence != null) loreTag.add(StringTag.valueOf(ofLoreJson("Residence: " + this.residence)));
        if (this.introduction != null) loreTag.add(StringTag.valueOf(ofLoreJson("Introduction: " + this.introduction)));
        if (this.sampleTimeMS != 0) loreTag.add(StringTag.valueOf(ofLoreJson("Sample Time: " + this.sampleTimeMS)));

        displayTag.put("Lore", loreTag);
        return of;
    }

    private String ofNameJson(String name) {
        JsonObject root = new JsonObject();
        root.addProperty("text", name);
        root.addProperty("italic", false);
        return JsonUtils.toJson(root);
    }

    private String ofLoreJson(String lore) {
        JsonObject root = new JsonObject();
        root.addProperty("text", lore);
        root.addProperty("italic", false);
        root.addProperty("color", "gold");
        return JsonUtils.toJson(root);
    }

    private String generateID() {
        String ret = null;
        while (ret == null || getWorkByID(ret) != null) {
            ret = UUID.randomUUID().toString().substring(0, 8);
        }
        return ret;
    }

    public static Work getWorkByID(String uuid) {
        ArrayList<Work> works = ConfigManager.worksWrapper.instance().works;
        for (Work work : works) {
            if (work.getId().equals(uuid)) {
                return work;
            }
        }
        return null;
    }

}
