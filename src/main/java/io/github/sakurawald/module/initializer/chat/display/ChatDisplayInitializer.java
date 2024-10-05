package io.github.sakurawald.module.initializer.chat.display;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PlaceholderHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.display.config.model.ChatDisplayConfigModel;
import io.github.sakurawald.module.initializer.chat.display.helper.DisplayHelper;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ChatDisplayInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<ChatDisplayConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, ChatDisplayConfigModel.class);

    private void registerEnderPlaceholder() {
        PlaceholderHelper.playerPlaceholder("ender",(player)->{
            String displayUUID = DisplayHelper.createEnderChestDisplay(player);
            return LocaleHelper.getTextByKey(player, "display.ender_chest.text")
                .copy()
                .fillStyle(
                    Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, LocaleHelper.getTextByKey(player, "display.click.prompt")))
                        .withClickEvent(makeDisplayClickEvent(displayUUID))
                );
        });
    }

    private void registerInvPlaceholder() {
        PlaceholderHelper.playerPlaceholder("inv",(player)->{
            String displayUUID = DisplayHelper.createInventoryDisplay(player);
            return LocaleHelper.getTextByKey(player, "display.inventory.text")
                .copy()
                .fillStyle(Style.EMPTY
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, LocaleHelper.getTextByKey(player, "display.click.prompt")))
                    .withClickEvent(makeDisplayClickEvent(displayUUID))
                );
        });
    }

    public void registerItemPlaceholder() {
        PlaceholderHelper.playerPlaceholder("item",(player)->{
            String displayUUID = DisplayHelper.createItemDisplay(player);

            MutableText text = LocaleHelper.getTextByKey(player, "display.item.text").copy();
            text = LocaleHelper.replaceText(text, "[item]", Text.translatable(player.getMainHandStack().getTranslationKey()));
            text.fillStyle(Style.EMPTY
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, LocaleHelper.getTextByKey(player, "display.click.prompt")))
                .withClickEvent(makeDisplayClickEvent(displayUUID))
            );
            return text;
        });

    }

    @NotNull
    private ClickEvent makeDisplayClickEvent(String displayUUID) {
        return Managers.getCallbackManager().makeCallback((player) -> {
            DisplayHelper.viewDisplay(player, displayUUID);
        }, config.getModel().expiration_duration_s, TimeUnit.SECONDS);
    }

    @Override
    public void onInitialize() {
        registerItemPlaceholder();
        registerInvPlaceholder();
        registerEnderPlaceholder();
    }
}
