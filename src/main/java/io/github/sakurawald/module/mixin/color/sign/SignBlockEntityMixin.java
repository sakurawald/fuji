package io.github.sakurawald.module.mixin.color.sign;

import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin {

    @ModifyVariable(method = "setText", at = @At("HEAD"), argsOnly = true)
    @NotNull
    SignText method(@NotNull SignText signText) {
        Text[] messages = signText.getMessages(false);
        Text[] newMessages = new Text[messages.length];
        for (int i = 0; i < messages.length; i++) {
            String string = PlainTextComponentSerializer.plainText().serialize(messages[i].asComponent());

            Component formated = MiniMessage.miniMessage().deserialize(string);
            newMessages[i] = MessageHelper.toText(formated);
        }

        return new SignText(newMessages, newMessages, signText.getColor(), signText.isGlowing());
    }
}
