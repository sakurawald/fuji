package io.github.sakurawald.module.initializer.chat.display;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.display.config.model.ChatDisplayConfigModel;
import io.github.sakurawald.module.initializer.chat.display.helper.DisplayHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ChatDisplayInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<ChatDisplayConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, ChatDisplayConfigModel.class);

    private void registerEnderPlaceholder() {
        Placeholders.register(
            Identifier.of(Fuji.MOD_ID, "ender"),
            (ctx, arg) -> {
                if (ctx.player() == null) PlaceholderResult.invalid();

                ServerPlayerEntity player = ctx.player();
                String displayUUID = DisplayHelper.createEnderChestDisplay(player);
                Component replacement =
                    LocaleHelper.getTextByKey(player, "display.ender_chest.text")
                        .asComponent()
                        .hoverEvent(LocaleHelper.getTextByKey(player, "display.click.prompt").asComponent())
                        .clickEvent(buildDisplayClickEvent(displayUUID));
                return PlaceholderResult.value(LocaleHelper.toText(replacement));
            });
    }

    private void registerInvPlaceholder() {
        Placeholders.register(
            Identifier.of(Fuji.MOD_ID, "inv"),
            (ctx, arg) -> {
                if (ctx.player() == null) PlaceholderResult.invalid();

                ServerPlayerEntity player = ctx.player();
                String displayUUID = DisplayHelper.createInventoryDisplay(player);
                Component replacement =
                    LocaleHelper.getTextByKey(player, "display.inventory.text")
                        .asComponent()
                        .hoverEvent(LocaleHelper.getTextByKey(player, "display.click.prompt").asComponent())
                        .clickEvent(buildDisplayClickEvent(displayUUID));

                return PlaceholderResult.value(LocaleHelper.toText(replacement));
            });
    }

    public void registerItemPlaceholder() {
        Placeholders.register(
            Identifier.of(Fuji.MOD_ID, "item"),
            (ctx, arg) -> {
                if (ctx.player() == null) PlaceholderResult.invalid();

                ServerPlayerEntity player = ctx.player();
                String displayUUID = DisplayHelper.createItemDisplay(player);

                Component component =
                    LocaleHelper.getTextByKey(player, "display.item.text")
                        .asComponent()
                        .replaceText(builder -> builder.matchLiteral("[item]").replacement(Component.translatable(player.getMainHandStack().getTranslationKey())))
                        .hoverEvent(LocaleHelper.getTextByKey(player, "display.click.prompt").asComponent())
                        .clickEvent(buildDisplayClickEvent(displayUUID));
                return PlaceholderResult.value(LocaleHelper.toText(component));
            });
    }

    @NotNull
    private ClickEvent buildDisplayClickEvent(String displayUUID) {
        return ClickEvent.callback(audience -> {
            if (audience instanceof ServerCommandSource css && css.getPlayer() != null) {
                DisplayHelper.viewDisplay(css.getPlayer(), displayUUID);
            }
        }, ClickCallback.Options.builder().lifetime(Duration.of(config.getModel().expiration_duration_s, ChronoUnit.SECONDS))
            .uses(Integer.MAX_VALUE).build());
    }

    @Override
    public void onInitialize() {
        registerItemPlaceholder();
        registerInvPlaceholder();
        registerEnderPlaceholder();
    }
}
