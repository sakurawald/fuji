package io.github.sakurawald.module.initializer.kit;

import io.github.sakurawald.config.annotation.Documentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Data
@AllArgsConstructor
@With
public class Kit {
    String name;
    List<ItemStack> stackList;
}
