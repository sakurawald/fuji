package io.github.sakurawald.module.mixin.chat.display;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.module.initializer.chat.display.ChatDisplayInitializer;
import io.github.sakurawald.module.initializer.chat.display.helper.DisplayHelper;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Unique
    private Text replaceDisplayText(Text original) {
        MutableText newValue
            = LocaleHelper.replaceText(original
            , ChatDisplayInitializer.config.model().replace_token.item_display_token
            , () -> DisplayHelper.createItemDisplayText(player));

        newValue
            = LocaleHelper.replaceText(newValue
            , ChatDisplayInitializer.config.model().replace_token.inv_display_token
            , () -> DisplayHelper.createInvDisplayText(player));

        newValue
            = LocaleHelper.replaceText(newValue
            , ChatDisplayInitializer.config.model().replace_token.ender_display_token
            , () -> DisplayHelper.createEnderDisplayText(player));
        return newValue;
    }

    @ModifyVariable(method = "sendChatMessage", at = @At(value = "HEAD"), argsOnly = true)
    public SignedMessage modifyChatMessageSentByPlayers(SignedMessage original) {
        Text newText = replaceDisplayText(original.getContent());
        return original.withUnsignedContent(newText);
    }

    /* some chat-related mods will encode the content into the sender, or vice versa.
    * For this reason, we have to parse the display text twice.
    *  */
    @ModifyVariable(method = "sendChatMessage", at = @At(value = "HEAD"), argsOnly = true)
    public MessageType.Parameters modifyChatMessageSentByPlayers(MessageType.Parameters original) {
        Text newText = replaceDisplayText(original.comp_920());
        return new MessageType.Parameters(original.type(), newText, original.comp_921());
    }
}
