package io.github.sakurawald.module.initializer.kit.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.gui.InputSignGui;
import io.github.sakurawald.core.gui.PagedGui;
import io.github.sakurawald.core.gui.layer.SingleLineLayer;
import io.github.sakurawald.module.initializer.kit.KitInitializer;
import io.github.sakurawald.module.initializer.kit.structure.Kit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KitEditorGui extends PagedGui<Kit> {

    public KitEditorGui(ServerPlayerEntity player, @NotNull List<Kit> entities, int pageIndex) {
        super(null, player, LocaleHelper.getTextByKey(player, "kit.gui.editor.title"), entities, pageIndex);

        /* make footer */
        SingleLineLayer footer = new SingleLineLayer();
        footer.setSlot(1, GuiHelper.makeHelpButton(player)
            .setLore(LocaleHelper.getTextListByKey(player, "kit.gui.editor.help.lore")));
        footer.setSlot(4, GuiHelper.makeAddButton(player).setCallback(() -> new InputSignGui(player, LocaleHelper.getTextByKey(player, "prompt.input.name")) {

            @Override
            public void onClose() {
                /* input kit name */
                String name = getLine(0).getString().trim();
                if (name.isEmpty()) {
                    LocaleHelper.sendActionBarByKey(player, "operation.cancelled");
                    return;
                }

                /* open edit kit gui */
                openKitEditingGui(getPlayer(), KitInitializer.readKit(name));
            }
        }.open()));
        this.addLayer(footer, 0, this.getHeight() - 1);
    }

    private void openKitEditingGui(@NotNull ServerPlayerEntity player, @NotNull Kit kit) {
        int rows = 5;
        SimpleInventory simpleInventory = new SimpleInventory(rows * 9);
        for (int i = 0; i < kit.getStackList().size(); i++) {
            simpleInventory.setStack(i, kit.getStackList().get(i));
        }

        /* set default items if the kit is empty */
        if (simpleInventory.isEmpty()) {
            simpleInventory.setStack(0, Items.IRON_SWORD.getDefaultStack());

            ItemStack food = Items.BREAD.getDefaultStack();
            food.setCount(16);
            simpleInventory.setStack(1, food);

            simpleInventory.setStack(36, Items.IRON_BOOTS.getDefaultStack());
            simpleInventory.setStack(37, Items.IRON_LEGGINGS.getDefaultStack());
            simpleInventory.setStack(38, Items.IRON_CHESTPLATE.getDefaultStack());
            simpleInventory.setStack(39, Items.IRON_HELMET.getDefaultStack());
            simpleInventory.setStack(40, Items.SHIELD.getDefaultStack());
        }

        /* put the forbidden zone */
        for (int i = 41; i <= 44; i++) {
            simpleInventory.setStack(i, GuiHelper.makeBarrier().getItemStack());
        }

        SimpleNamedScreenHandlerFactory simpleNamedScreenHandlerFactory = new SimpleNamedScreenHandlerFactory((i, playerInventory, p) ->
            new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, i, playerInventory, simpleInventory, rows) {
                @Override
                public void onSlotClick(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
                    // note: skip BARRIER item stack click.
                    if (GuiHelper.isInvalidSlotInPlayerInventory(i)) return;
                    super.onSlotClick(i, j, slotActionType, playerEntity);
                }

                @Override
                public void onClosed(PlayerEntity playerEntity) {
                    super.onClosed(playerEntity);
                    List<ItemStack> itemStacks = new ArrayList<>();
                    for (int j = 0; j < simpleInventory.size(); j++) {
                        itemStacks.add(simpleInventory.getStack(j));
                    }
                    KitInitializer.writeKit(kit.withStackList(itemStacks));
                }

            }, LocaleHelper.getTextByKey(player, "kit.gui.editor.kit.title", kit.getName()));
        player.openHandledScreen(simpleNamedScreenHandlerFactory);
    }

    @Override
    public PagedGui<Kit> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<Kit> entities, int pageIndex) {
        return new KitEditorGui(player, entities, pageIndex);
    }

    @Override
    public GuiElementInterface toGuiElement(@NotNull Kit entity) {
        return new GuiElementBuilder().setItem(Items.CHEST)
            .setName(Text.literal(entity.getName()))
            .setCallback((event) -> {

                if (event.isLeft) {
                    openKitEditingGui(getPlayer(), entity);
                }

                if (event.shift && event.isRight) {
                    KitInitializer.deleteKit(entity.getName());
                    deleteEntity(entity);
                    LocaleHelper.sendActionBarByKey(getPlayer(), "deleted");
                }

            })
            .build();
    }

    @Override
    public @NotNull List<Kit> filter(@NotNull String keyword) {
        return getEntities().stream().filter(e -> e.getName().contains(keyword)).toList();
    }
}
