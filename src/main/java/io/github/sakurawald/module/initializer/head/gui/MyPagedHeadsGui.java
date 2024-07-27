package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.module.common.gui.PagedGui;
import io.github.sakurawald.module.initializer.head.HeadInitializer;
import io.github.sakurawald.module.initializer.head.structure.EconomyType;
import io.github.sakurawald.module.initializer.head.structure.Head;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MyPagedHeadsGui extends PagedGui<Head> {

    public MyPagedHeadsGui(SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<Head> entities, int pageIndex) {
        super(parent, player, title, entities, pageIndex);
    }

    @Override
    public PagedGui<Head> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<Head> entities, int pageIndex) {
        return new MyPagedHeadsGui(parent, player, title, entities, pageIndex);
    }

    @Override
    public GuiElementInterface toGuiElement(Head entity) {
        var builder = GuiElementBuilder.from(entity.of());
        if (HeadInitializer.headHandler.model().economyType != EconomyType.FREE) {
            builder.addLoreLine(Text.empty());
            builder.addLoreLine(MessageHelper.ofText(getPlayer(), "head.price").copy().append(EconomyType.getCost()));
        }
        builder.setCallback((index, type, action) -> {
            processHeadClick(entity, type);
        });
        return builder.build();
    }

    @Override
    public List<Head> filter(String keywords) {
        return getEntities().stream()
                .filter(head -> head.name.toLowerCase().contains(keywords.toLowerCase())
                        || head.getTagsOrEmpty().toLowerCase().contains(keywords.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void processHeadClick(@NotNull Head head, @NotNull ClickType type) {
        ServerPlayerEntity player = getPlayer();

        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();
        ItemStack headStack = head.of();

        if (cursorStack.isEmpty()) {
            if (type.shift) {
                EconomyType.tryPurchase(player, 1, () -> player.getInventory().insertStack(headStack));
            } else if (type.isMiddle) {
                EconomyType.tryPurchase(player, headStack.getMaxCount(), () -> {
                    headStack.setCount(headStack.getMaxCount());
                    player.currentScreenHandler.setCursorStack(headStack);
                });
            } else {
                EconomyType.tryPurchase(player, 1, () -> player.currentScreenHandler.setCursorStack(headStack));
            }
        } else if (cursorStack.getMaxCount() <= cursorStack.getCount()) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (ItemStack.areItemsAndComponentsEqual(headStack, cursorStack)) {
            if (type.isLeft) {
                EconomyType.tryPurchase(player, 1, () -> cursorStack.increment(1));
            } else if (type.isRight) {
                if (HeadInitializer.headHandler.model().economyType == EconomyType.FREE)
                    cursorStack.decrement(1);
            } else if (type.isMiddle) {
                var amount = headStack.getMaxCount() - cursorStack.getCount();
                EconomyType.tryPurchase(player, amount, () -> {
                    headStack.setCount(headStack.getMaxCount());
                    player.currentScreenHandler.setCursorStack(headStack);
                });
            }
        } else {
            if (HeadInitializer.headHandler.model().economyType ==EconomyType.FREE)
                player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }
    }
}
