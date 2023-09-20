package fun.sakurawald.module.works;

import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

@AllArgsConstructor
public class WorkHopper {
    public static final HashMap<BlockPos , WorkHopper> workHopper = new HashMap<>();

    private final Work work;
    private final BlockPos blockPos;

    public void addItemStack(ItemStack itemStack) {
        HashMap<String, Long> counter = this.work.counter;
        String key = itemStack.getDescriptionId();
        counter.put(key, counter.getOrDefault(key, 0L) + 1);
    }
}
