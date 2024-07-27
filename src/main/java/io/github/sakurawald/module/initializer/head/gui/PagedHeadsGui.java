package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.GuiInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import eu.pb4.sgui.api.gui.layered.LayeredGui;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.head.HeadInitializer;
import io.github.sakurawald.module.initializer.head.structure.EconomyType;
import io.github.sakurawald.module.initializer.head.structure.Head;
import io.github.sakurawald.util.minecraft.GuiHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PagedHeadsGui extends LayeredGui {

    /**
     * Constructs a new layered container gui for the supplied player.
     *
     * @param type                  the screen handler that the client should display
     * @param player                the player to server this gui to
     * @param manipulatePlayerSlots if <code>true</code> the players inventory
     *                              will be treated as slots of this gui
     */
    public PagedHeadsGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
    }
}
