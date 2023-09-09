package fun.sakurawald.mixin.zero_command_permission;

import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerCommandSource.class)
public interface CommandSourceStackAccessor {

    @Accessor
    int getLevel();

    @Accessor
    @Mutable
    void setSilent(boolean silent);

}
