package io.github.sakurawald.module.mixin.color.sign;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
@Slf4j
public abstract class ServerPlayerEntityMixin {

    @Unique
    ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;


    @Inject(method = "openEditSignScreen", at = @At("HEAD"))
    private void $onUse(SignBlockEntity signBlockEntity, boolean bl, CallbackInfo ci) {
        if (ci.isCancelled()) return;

        boolean facing = signBlockEntity.isPlayerFacingFront(player);
        SignText signText = signBlockEntity.getText(facing);

        Text[] texts = new Text[4];
        Text[] messages = signText.getMessages(false);
        for (int i = 0; i < messages.length; i++) {
            texts[i] = Text.literal(MiniMessage.miniMessage().serialize(messages[i].asComponent()).replace("<","\\<"));
        }

        // note: update the sign text in server-side before the client-side open the sign editor
        SignText ret = new SignText(texts, texts, signText.getColor(), signText.isGlowing());
        signBlockEntity.setText(ret, facing);
        player.networkHandler.sendPacket(signBlockEntity.toUpdatePacket());
    }

}
