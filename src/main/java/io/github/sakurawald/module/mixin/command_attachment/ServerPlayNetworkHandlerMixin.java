package io.github.sakurawald.module.mixin.command_attachment;

import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.command_attachment.CommandAttachmentInitializer;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.InteractType;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.NbtHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Unique
    ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Unique
    private static final CommandAttachmentInitializer module = Managers.getModuleManager().getInitializer(CommandAttachmentInitializer.class);

    @Inject(method = "swingHand", at = @At("HEAD"))
    void onPlayerLeftClick(Hand hand, CallbackInfo ci) {
        if (hand.equals(Hand.MAIN_HAND)) {
            String uuid = NbtHelper.getUuid(player.getMainHandStack().get(DataComponentTypes.CUSTOM_DATA));
            if (uuid == null) return;

            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            module.trigger(uuid, player, List.of(InteractType.LEFT, InteractType.BOTH));
        }

    }

}
