package fun.sakurawald.mixin.zero_command_permission;

import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(value = CommandNode.class, remap = false)
public interface CommandNodeAccessor<S> {

    @Accessor
    @Mutable
    void setRequirement(Predicate<S> predicate);

}
