package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import eu.pb4.sgui.api.gui.layered.LayeredGui;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.head.HeadModule;
import io.github.sakurawald.module.initializer.head.api.Head;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import java.util.List;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PagedHeadsGui extends LayeredGui {
    public final List<Head> heads;
    final GuiInterface parent;
    final Layer contentLayer;
    final Layer navigationLayer;
    final HeadModule module = ModuleManager.getInitializer(HeadModule.class);
    public int page = 0;

    public PagedHeadsGui(GuiInterface parent, List<Head> heads) {
        super(ScreenHandlerType.GENERIC_9X6, parent.getPlayer(), false);
        this.heads = heads;
        this.parent = parent;
        this.contentLayer = new Layer(5, 9);
        updateContent();
        this.addLayer(contentLayer, 0, 0);
        this.navigationLayer = new Layer(1, 9);
        this.updateNavigation();
        this.addLayer(navigationLayer, 0, 5);
    }

    private int getMaxPage() {
        return Math.max(1, (int) Math.ceil((double) this.heads.size() / 45));
    }

    private void updatePage() {
        this.updateNavigation();
        this.updateContent();
    }

    private void updateNavigation() {
        for (int i = 0; i < 9; i++) {
            navigationLayer.setSlot(i, Items.PINK_STAINED_GLASS_PANE.getDefaultStack());
        }
        navigationLayer.setSlot(
                3, new Head(
                        UUID.fromString("8aa062dc-9852-42b1-ae37-b2f8a3121c0e"),
                        GuiUtil.PREVIOUS_PAGE_ICON).of().setCustomName(MessageUtil.ofVomponent(parent.getPlayer(), "previous_page")),
                ((index, type, action) -> {
                    this.page -= 1;
                    if (this.page < 0) {
                        this.page = 0;
                    }

                    this.updatePage();
                })
        );
        navigationLayer.setSlot(
                5, new Head(
                        UUID.fromString("8aa062dc-9852-42b1-ae37-b2f8a3121c0e"),
                        GuiUtil.NEXT_PAGE_ICON).of().setCustomName(MessageUtil.ofVomponent(parent.getPlayer(), "next_page")),
                ((index, type, action) -> {
                    this.page += 1;
                    if (this.page >= getMaxPage()) {
                        this.page = getMaxPage() - 1;
                    }
                    this.updatePage();
                })
        );
        navigationLayer.setSlot(4, new GuiElementBuilder(Items.PLAYER_HEAD)
                .setSkullOwner(GuiUtil.QUESTION_MARK_ICON)
                .setName(MessageUtil.ofVomponent(parent.getPlayer(), "head.page", this.page + 1, this.getMaxPage()))
        );
    }

    private void updateContent() {
        for (int i = 0; i < 45; i++) {
            if (heads.size() > i + (this.page * 45)) {
                Head head = heads.get(i + (this.page * 45));
                var builder = GuiElementBuilder.from(head.of());
                if (Configs.headHandler.model().economyType != HeadModule.EconomyType.FREE) {
                    builder.addLoreLine(Text.empty());
                    builder.addLoreLine(MessageUtil.ofVomponent(parent.getPlayer(), "head.price").copy().append(module.getCost()));
                }

                contentLayer.setSlot(i, builder.asStack(), (index, type, action) -> processHeadClick(head, type));
            } else {
                contentLayer.setSlot(i, Items.AIR.getDefaultStack());
            }
        }
    }

    private void processHeadClick(Head head, ClickType type) {
        var player = getPlayer();

        ItemStack cursorStack = getPlayer().currentScreenHandler.getCursorStack();
        ItemStack headStack = head.of();

        if (cursorStack.isEmpty()) {
            if (type.shift) {
                module.tryPurchase(player, 1, () -> player.getInventory().insertStack(headStack));
            } else if (type.isMiddle) {
                module.tryPurchase(player, headStack.getMaxCount(), () -> {
                    headStack.setCount(headStack.getMaxCount());
                    player.currentScreenHandler.setCursorStack(headStack);
                });
            } else {
                module.tryPurchase(player, 1, () -> player.currentScreenHandler.setCursorStack(headStack));
            }
        } else if (cursorStack.getMaxCount() <= cursorStack.getCount()) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (ItemStack.canCombine(headStack, cursorStack)) {
            if (type.isLeft) {
                module.tryPurchase(player, 1, () -> cursorStack.increment(1));
            } else if (type.isRight) {
                if (Configs.headHandler.model().economyType == HeadModule.EconomyType.FREE)
                    cursorStack.decrement(1);
            } else if (type.isMiddle) {
                var amount = headStack.getMaxCount() - cursorStack.getCount();
                module.tryPurchase(player, amount, () -> {
                    headStack.setCount(headStack.getMaxCount());
                    player.currentScreenHandler.setCursorStack(headStack);
                });
            }
        } else {
            if (Configs.headHandler.model().economyType == HeadModule.EconomyType.FREE)
                player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }
    }

    @Override
    public void onClose() {
        parent.open();
    }
}
