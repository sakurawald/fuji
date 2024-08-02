package io.github.sakurawald.module.initializer.chat.display;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.display.helper.DisplayHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
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

    private void registerEnderPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "ender"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();
                    String displayUUID = DisplayHelper.createEnderChestDisplay(player);
                    Component replacement =
                            MessageHelper.ofComponent(player, "display.ender_chest.text")
                                    .hoverEvent(MessageHelper.ofComponent(player, "display.click.prompt"))
                                    .clickEvent(buildDisplayClickEvent(displayUUID));
                    return PlaceholderResult.value(MessageHelper.toText(replacement));
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
                            MessageHelper.ofComponent(player, "display.inventory.text")
                                    .hoverEvent(MessageHelper.ofComponent(player, "display.click.prompt"))
                                    .clickEvent(buildDisplayClickEvent(displayUUID));

                    return PlaceholderResult.value(MessageHelper.toText(replacement));
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
                            MessageHelper.ofComponent(player, "display.item.text")
                                    .replaceText(builder -> builder.matchLiteral("[item]").replacement(Component.translatable(player.getMainHandStack().getTranslationKey())))
                                    .hoverEvent(MessageHelper.ofComponent(player, "display.click.prompt"))
                                    .clickEvent(buildDisplayClickEvent(displayUUID));
                    return PlaceholderResult.value(MessageHelper.toText(component));
                });
    }

    @NotNull
    private ClickEvent buildDisplayClickEvent(String displayUUID) {
        return ClickEvent.callback(audience -> {
            if (audience instanceof ServerCommandSource css && css.getPlayer() != null) {
                DisplayHelper.viewDisplay(css.getPlayer(), displayUUID);
            }
        }, ClickCallback.Options.builder().lifetime(Duration.of(Configs.configHandler.model().modules.chat.display.expiration_duration_s, ChronoUnit.SECONDS))
                .uses(Integer.MAX_VALUE).build());
    }

    @Override
    public void onInitialize() {
        registerItemPlaceholder();
        registerInvPlaceholder();
        registerEnderPlaceholder();
    }
}
