package io.github.sakurawald.module.mixin.afk;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.sakurawald.auxiliary.minecraft.MessageHelper.*;

// to override tab list name in `tab list module`
@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 250)
public abstract class ServerPlayerMixin implements AfkStateAccessor {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    private boolean afk = false;

    @Unique
    private long lastLastActionTime = 0;

    @ModifyReturnValue(method = "getPlayerListName", at = @At("RETURN"))
    public Text $getPlayerListName(Text original) {
        AfkStateAccessor accessor = (AfkStateAccessor) player;
        if (accessor.fuji$isAfk()) {
           return ofText(player, false, Configs.configHandler.model().modules.afk.format);
        }

        return original;
    }

    @Inject(method = "updateLastActionTime", at = @At("HEAD"))
    public void $updateLastActionTime(CallbackInfo ci) {
        if (fuji$isAfk()) {
            fuji$setAfk(false);
        }
    }

    @Override
    public void fuji$setAfk(boolean flag) {
        this.afk = flag;
        this.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayerEntity) (Object) this));
        MessageHelper.sendBroadcast(this.afk ? "afk.on.broadcast" : "afk.off.broadcast", this.player.getGameProfile().getName());
    }

    @Override
    public boolean fuji$isAfk() {
        return this.afk;
    }

    @Override
    public void fuji$setLastLastActionTime(long lastActionTime) {
        this.lastLastActionTime = lastActionTime;
    }

    @Override
    public long fuji$getLastLastActionTime() {
        return this.lastLastActionTime;
    }

}
