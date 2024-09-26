package io.github.sakurawald.module.initializer.head.gui;

import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.service.gameprofile_fetcher.MojangProfileFetcher;
import io.github.sakurawald.module.initializer.head.HeadInitializer;
import io.github.sakurawald.module.initializer.head.structure.EconomyType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PlayerHeadInputGui extends AnvilInputGui {

    private final @NotNull SimpleGui parentGui;
    private long apiDebounce = 0;

    public PlayerHeadInputGui(@NotNull HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;
        this.setTitle(LocaleHelper.getTextByKey(player, "head.category.player"));
        this.setSlot(1, GuiHelper.makeBarrier());
        this.resetResultSlot();
    }

    @Override
    public void setDefaultInputValue(String input) {
        this.setSlot(0, GuiHelper.makeBarrier());
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        apiDebounce = System.currentTimeMillis() + 500;
    }

    @Override
    public void onClose() {
        parentGui.open();
    }

    private void resetResultSlot() {
        this.setSlot(2, GuiHelper.makeBarrier());
    }

    @Override
    public void onTick() {
        if (apiDebounce != 0 && apiDebounce <= System.currentTimeMillis()) {
            apiDebounce = 0;

            CompletableFuture.runAsync(() -> {
                GameProfile gameProfile = MojangProfileFetcher.makeGameProfile(this.getInput());
                GuiElementBuilder builder = new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setSkullOwner(gameProfile, player.server);

                if (HeadInitializer.headHandler.getModel().economy_type != EconomyType.FREE) {
                    builder.addLoreLine(Text.empty());
                    builder.addLoreLine(LocaleHelper.getTextByKey(player, "head.price").copy().append(EconomyType.getCost()));
                }
                ItemStack resultStack = builder.asStack();

                this.setSlot(2, resultStack, (index, type, action, gui) ->
                    EconomyType.tryPurchase(player, 1, () -> {
                        var cursorStack = getPlayer().currentScreenHandler.getCursorStack();
                        if (player.currentScreenHandler.getCursorStack().isEmpty()) {
                            player.currentScreenHandler.setCursorStack(resultStack.copy());
                        } else if (ItemStack.areItemsAndComponentsEqual(resultStack, cursorStack) && cursorStack.getCount() < cursorStack.getMaxCount()) {
                            cursorStack.increment(1);
                        } else {
                            player.dropItem(resultStack.copy(), false);
                        }
                    })
                );

            });
        }
    }

}
