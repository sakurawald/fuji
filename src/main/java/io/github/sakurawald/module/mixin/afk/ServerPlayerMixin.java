package io.github.sakurawald.module.mixin.afk;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import io.github.sakurawald.auxiliary.LogUtil;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.afk.AfkInitializer;
import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static io.github.sakurawald.auxiliary.minecraft.MessageHelper.ofText;

// to override tab list name in `tab list module`
@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 250)
public abstract class ServerPlayerMixin extends PlayerEntity implements AfkStateAccessor {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    private boolean afk = false;

    @Unique
    private long lastLastActionTime = 0;

    public ServerPlayerMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

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

        // trigger event
        ConfigModel.Modules.Afk.AfkEvent afkEvent = Configs.configHandler.model().modules.afk.afk_event;
        List<String> commandList = this.afk ? afkEvent.on_enter_afk : afkEvent.on_leave_afk;
        CommandExecutor.executeSpecializedCommand(player, commandList);
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

    @Override
    public void move(MovementType movementType, Vec3d vec3d) {
        if (AfkInitializer.isPlayerActuallyMovedItself(movementType,vec3d)) {
            fuji$setAfk(false);
            super.move(movementType, vec3d);
        }

        super.move(movementType, vec3d);
    }
}
