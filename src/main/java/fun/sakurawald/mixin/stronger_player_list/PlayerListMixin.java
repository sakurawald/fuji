package fun.sakurawald.mixin.stronger_player_list;

import fun.sakurawald.module.stronger_player_list.PlayerListAccessor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(PlayerList.class)
@Slf4j
public abstract class PlayerListMixin implements PlayerListAccessor {

    @Mutable
    @Final
    @Shadow
    private List<ServerPlayer> players;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void patchStrongerPlayerList() {
        players = new CopyOnWriteArrayList<>() {
            {
                log.warn("Patch stronger player list.");
            }
        };
    }

}