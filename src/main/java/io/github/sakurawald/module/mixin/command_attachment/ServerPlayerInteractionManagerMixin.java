package io.github.sakurawald.module.mixin.command_attachment;

import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.command_attachment.CommandAttachmentInitializer;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.InteractType;
import io.github.sakurawald.auxiliary.minecraft.NbtHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Unique
    private static final CommandAttachmentInitializer module = Managers.getModuleManager().getInitializer(CommandAttachmentInitializer.class);

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "interactItem", at = @At("HEAD"))
    void $interactItem(ServerPlayerEntity serverPlayerEntity, World world, @NotNull ItemStack itemStack, Hand hand, @NotNull CallbackInfoReturnable<ActionResult> cir) {
        String uuid = NbtHelper.getUuid(itemStack.get(DataComponentTypes.CUSTOM_DATA));
        if (uuid == null) return;

        module.trigger(uuid, player, List.of(InteractType.RIGHT, InteractType.BOTH));
    }

}
