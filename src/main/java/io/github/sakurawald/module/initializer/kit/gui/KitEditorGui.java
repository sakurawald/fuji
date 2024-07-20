package io.github.sakurawald.module.initializer.kit.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.gui.InputSignGui;
import io.github.sakurawald.module.common.gui.PagedGui;
import io.github.sakurawald.module.common.gui.layer.SingleLineLayer;
import io.github.sakurawald.module.initializer.kit.Kit;
import io.github.sakurawald.module.initializer.kit.KitInitializer;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class KitEditorGui extends PagedGui<Kit> {

    private static final KitInitializer module = ModuleManager.getInitializer(KitInitializer.class);

    public KitEditorGui(ServerPlayerEntity player, List<Kit> entities) {
        super(player, MessageUtil.ofText(player, true, "kit.gui.editor.title"), entities);
    }

    private void openEditorGui(ServerPlayerEntity player, Kit kit) {
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
                        if (i == 41 || i == 42 || i == 43 || i == 44) return;
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

                }, MessageUtil.ofText(player, true, "kit.gui.editor.kit.title", kit.getName()));

        player.openHandledScreen(simpleNamedScreenHandlerFactory);
    }

    @Override
    public void onConstructor(PagedGui<Kit> parent) {
        SingleLineLayer singleLineLayer = new SingleLineLayer();
        singleLineLayer.setSlot(1, GuiUtil.createHelpButton(getPlayer())
                .setLore(Collections.singletonList(Text.literal("hello"))));

        singleLineLayer.setSlot(4, GuiUtil.createAddButton(getPlayer()).setCallback(() -> {
            new InputSignGui(getPlayer(), "prompt.input.name") {
                @Override
                public void onClose() {
                    String name = getLine(0).getString().trim();
                    if (name.isEmpty()) {
                        MessageUtil.sendMessage(player, "operation.cancelled");
                        return;
                    }

                    openEditorGui(getPlayer(), module.readKit(name));
                }
            }.open();
        }));
        parent.addLayer(singleLineLayer, 0, parent.getHeight() - 1);
    }

    @Override
    public GuiElementInterface toGuiElement(Kit entity) {
        return new GuiElementBuilder().setItem(Items.CHEST)
                .setName(Text.literal(entity.getName()))
                .setCallback(() -> openEditorGui(getPlayer(), entity))
                .build();
    }

    @Override
    public List<Kit> filter(String keyword) {
        return getEntities().stream().filter(e -> e.toString().contains(keyword)).toList();
    }
}
