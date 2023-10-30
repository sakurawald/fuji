package io.github.sakurawald.mixin.better_fake_player;

import carpet.patches.EntityPlayerMPFake;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


// the carpet-fabric default event handler priority is 1000
@Mixin(value = Player.class, priority = 999)

public abstract class PlayerMixin extends LivingEntity {

    @Unique
    private static final BetterFakePlayerModule betterFakePlayerModule = ModuleManager.getOrNewInstance(BetterFakePlayerModule.class);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void $interactOn(Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (target instanceof EntityPlayerMPFake fakePlayer) {
            ServerPlayer source = (ServerPlayer) (Object) this;
            if (!betterFakePlayerModule.isMyFakePlayer(source, fakePlayer)) {
                // cancel this event
                cir.setReturnValue(InteractionResult.FAIL);

                // main-hand and off-hand will both trigger this event
                if (hand == InteractionHand.MAIN_HAND) {
                    MessageUtil.sendMessage(source, "better_fake_player.manipulate.forbidden");
                }
            }
        }
    }

}
