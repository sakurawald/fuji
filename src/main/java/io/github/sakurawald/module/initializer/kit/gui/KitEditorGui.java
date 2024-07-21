package io.github.sakurawald.module.initializer.kit.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import eu.pb4.sgui.api.gui.layered.LayerView;
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
import java.util.List;

@Slf4j
public class KitEditorGui extends PagedGui<Kit> {

    private static final KitInitializer module = ModuleManager.getInitializer(KitInitializer.class);

    public KitEditorGui(ServerPlayerEntity player, List<Kit> entities) {
        super(player, MessageUtil.ofText(player, true, "kit.gui.editor.title"), entities);
    }

    private void openEditKitGui(ServerPlayerEntity player, Kit kit) {
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
        ServerPlayerEntity player = getPlayer();

        SingleLineLayer singleLineLayer = new SingleLineLayer();
        singleLineLayer.setSlot(1, GuiUtil.createHelpButton(player)
                .setLore(MessageUtil.ofTextList(player, "kit.gui.editor.help.lore")));
        singleLineLayer.setSlot(4, GuiUtil.createAddButton(player).setCallback(() -> new InputSignGui(player, "prompt.input.name") {
            @Override
            public void onClose() {
                String name = getLine(0).getString().trim();
                if (name.isEmpty()) {
                    MessageUtil.sendActionBar(player, "operation.cancelled");
                    return;
                }

                openEditKitGui(getPlayer(), module.readKit(name));
            }
        }.open()));
        parent.addLayer(singleLineLayer, 0, parent.getHeight() - 1);
    }

    @Override
    public GuiElementInterface toGuiElement(PagedGui<Kit> ref, Kit entity) {
        return new GuiElementBuilder().setItem(Items.CHEST)
                .setName(Text.literal(entity.getName()))
                .setCallback((event) -> {

                    if (event.isLeft) {
                        openEditKitGui(getPlayer(), entity);
                    }

                    if (event.shift && event.isRight) {
                        module.deleteKit(entity.getName());
                        MessageUtil.sendActionBar(getPlayer(),"deleted");

                        deleteEntity(entity);
                    }

                })
                .build();
    }

    @Override
    public List<Kit> filter(String keyword) {
        return getEntities().stream().filter(e -> e.toString().contains(keyword)).toList();
    }
}
