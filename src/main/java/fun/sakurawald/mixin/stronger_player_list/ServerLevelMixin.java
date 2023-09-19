package fun.sakurawald.mixin.stronger_player_list;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ServerLevel.class)
@Slf4j
public abstract class ServerLevelMixin {

    @Mutable
    @Final
    @Shadow
    List<ServerPlayer> players;

    @Inject(method = "<init>", at = @At("TAIL"), require = 1)
    private void $init(CallbackInfo ci) {
        ServerLevel that = (ServerLevel) (Object) this;
        players = new CopyOnWriteArrayList<>() {
            {
                log.warn("Patch stronger player list for {}", that.dimension().location());
            }
        };
    }

}