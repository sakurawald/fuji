package fun.sakurawald.module.teleport_warmup;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

public interface ServerPlayerAccessor {

    boolean sakurawald$inCombat();

}
