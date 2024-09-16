package io.github.sakurawald.module.initializer.kit.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.gui.InputSignGui;
import io.github.sakurawald.core.gui.PagedGui;
import io.github.sakurawald.core.gui.layer.SingleLineLayer;
import io.github.sakurawald.core.manager.Managers;
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

    private static final KitInitializer module = Managers.getModuleManager().getInitializer(KitInitializer.class);

    public KitEditorGui(ServerPlayerEntity player, @NotNull List<Kit> entities, int pageIndex) {
        super(null, player, LocaleHelper.getTextByKey(player, "kit.gui.editor.title"), entities, pageIndex);

        SingleLineLayer singleLineLayer = new SingleLineLayer();
        singleLineLayer.setSlot(1, GuiHelper.makeHelpButton(player)
                .setLore(LocaleHelper.getTextListByKey(player, "kit.gui.editor.help.lore")));
        singleLineLayer.setSlot(4, GuiHelper.makeAddButton(player).setCallback(() -> new InputSignGui(player, "prompt.input.name") {
            @Override
            public void onClose() {
                String name = getLine(0).getString().trim();
                if (name.isEmpty()) {
                    LocaleHelper.sendActionBarByKey(player, "operation.cancelled");
                    return;
                }

                openEditKitGui(getPlayer(), module.readKit(name));
            }
        }.open()));
        this.addLayer(singleLineLayer, 0, this.getHeight() - 1);
    }

    private void openEditKitGui(@NotNull ServerPlayerEntity player, @NotNull Kit kit) {
        int rows = 5;
        SimpleInventory simpleInventory = new SimpleInventory(rows * 9);
        for (int i = 0; i < kit.getStackList().size(); i++) {
            simpleInventory.setStack(i, kit.getStackList().get(i));
        }

        // forbidden
        for (int i = 41; i <= 44; i++) {
            simpleInventory.setStack(i, Items.BARRIER.getDefaultStack());
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
                        module.writeKit(kit.withStackList(itemStacks));
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
                        openEditKitGui(getPlayer(), entity);
                    }

                    if (event.shift && event.isRight) {
                        module.deleteKit(entity.getName());
                        LocaleHelper.sendActionBarByKey(getPlayer(), "deleted");

                        deleteEntity(entity);
                    }

                })
                .build();
    }

    @Override
    public @NotNull List<Kit> filter(@NotNull String keyword) {
        return getEntities().stream().filter(e -> e.toString().contains(keyword)).toList();
    }
}
