package io.github.sakurawald.module.initializer.kit.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import net.minecraft.item.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
@With
public class Kit {
    String name;
    List<ItemStack> stackList;
}
