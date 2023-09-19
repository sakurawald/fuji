package fun.sakurawald.mixin.stronger_player_list;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(PlayerList.class)
@Slf4j
public abstract class PlayerListMixin {

    @Mutable
    @Final
    @Shadow
    private List<ServerPlayer> players;

    @Inject(method = "<init>", at = @At("TAIL"), require = 1)
    private void $init(CallbackInfo ci) {
        players = new CopyOnWriteArrayList<>() {
            {
                log.info("Patch stronger player list for Server#PlayerList");
            }
        };
    }

}