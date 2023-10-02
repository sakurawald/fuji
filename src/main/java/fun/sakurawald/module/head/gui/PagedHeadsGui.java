package fun.sakurawald.module.head.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import eu.pb4.sgui.api.gui.layered.LayeredGui;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.config.HeadGSON;
import fun.sakurawald.module.head.HeadModule;
import fun.sakurawald.module.head.api.Head;
import fun.sakurawald.util.GuiUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.UUID;

import static fun.sakurawald.util.MessageUtil.ofVomponent;

public class PagedHeadsGui extends LayeredGui {
    public final List<Head> heads;
    final GuiInterface parent;
    final Layer contentLayer;
    final Layer navigationLayer;
    public int page = 0;

    public PagedHeadsGui(GuiInterface parent, List<Head> heads) {
        super(MenuType.GENERIC_9x6, parent.getPlayer(), false);
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
            navigationLayer.setSlot(i, Items.PINK_STAINED_GLASS_PANE.getDefaultInstance());
        }
        navigationLayer.setSlot(
                3, new Head(
                        UUID.fromString("8aa062dc-9852-42b1-ae37-b2f8a3121c0e"),
                        GuiUtil.PREVIOUS_PAGE_ICON).of().setHoverName(ofVomponent(parent.getPlayer(), "previous_page")),
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
                        GuiUtil.NEXT_PAGE_ICON).of().setHoverName(ofVomponent(parent.getPlayer(), "next_page")),
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
                .setName(ofVomponent(parent.getPlayer(), "head.page", this.page + 1, this.getMaxPage()))
        );
    }

    private void updateContent() {
        for (int i = 0; i < 45; i++) {
            if (heads.size() > i + (this.page * 45)) {
                Head head = heads.get(i + (this.page * 45));
                var builder = GuiElementBuilder.from(head.of());
                if (ConfigManager.headWrapper.instance().economyType != HeadGSON.EconomyType.FREE) {
                    builder.addLoreLine(Component.empty());
                    builder.addLoreLine(ofVomponent(parent.getPlayer(), "head.price").copy().append(ConfigManager.headWrapper.instance().getCost()));
                }

                contentLayer.setSlot(i, builder.asStack(), (index, type, action) -> processHeadClick(head, type));
            } else {
                contentLayer.setSlot(i, Items.AIR.getDefaultInstance());
            }
        }
    }

    private void processHeadClick(Head head, ClickType type) {
        var player = getPlayer();

        ItemStack cursorStack = getPlayer().containerMenu.getCarried();
        ItemStack headStack = head.of();

        if (cursorStack.isEmpty()) {
            if (type.shift) {
                HeadModule.tryPurchase(player, 1, () -> player.getInventory().add(headStack));
            } else if (type.isMiddle) {
                HeadModule.tryPurchase(player, headStack.getMaxStackSize(), () -> {
                    headStack.setCount(headStack.getMaxStackSize());
                    player.containerMenu.setCarried(headStack);
                });
            } else {
                HeadModule.tryPurchase(player, 1, () -> player.containerMenu.setCarried(headStack));
            }
        } else if (cursorStack.getMaxStackSize() <= cursorStack.getCount()) {
            //noinspection UnnecessaryReturnStatement
            return;
        } else if (ItemStack.isSameItemSameTags(headStack, cursorStack)) {
            if (type.isLeft) {
                HeadModule.tryPurchase(player, 1, () -> cursorStack.grow(1));
            } else if (type.isRight) {
                if (ConfigManager.headWrapper.instance().economyType == HeadGSON.EconomyType.FREE)
                    cursorStack.shrink(1);
            } else if (type.isMiddle) {
                var amount = headStack.getMaxStackSize() - cursorStack.getCount();
                HeadModule.tryPurchase(player, amount, () -> {
                    headStack.setCount(headStack.getMaxStackSize());
                    player.containerMenu.setCarried(headStack);
                });
            }
        } else {
            if (ConfigManager.headWrapper.instance().economyType == HeadGSON.EconomyType.FREE)
                player.containerMenu.setCarried(ItemStack.EMPTY);
        }
    }

    @Override
    public void onClose() {
        parent.open();
    }
}
