package fun.sakurawald.module.works;

import fun.sakurawald.module.works.work_type.Work;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BlockPosCache {
    @Getter
    private static final ConcurrentHashMap<BlockPos, HashSet<Work>> blockpos2works = new ConcurrentHashMap<>();

    public static void bind(BlockPos blockPos, Work work) {
        blockpos2works.computeIfAbsent(blockPos, k -> new HashSet<>()).add(work);
    }

    public static void unbind(Work work) {
        for (Map.Entry<BlockPos, HashSet<Work>> entry : blockpos2works.entrySet()) {
            // remove cache
            entry.getValue().remove(work);
            if (entry.getValue().isEmpty()) {
                blockpos2works.remove(entry.getKey());
            }
        }
    }
}
