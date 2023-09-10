package fun.sakurawald.mixin.command_cooldown;

import com.mojang.brigadier.CommandDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(CommandDispatcher.class)
public class CommandDispatcherMixin {

    @Inject(method = "onCommand")

}
