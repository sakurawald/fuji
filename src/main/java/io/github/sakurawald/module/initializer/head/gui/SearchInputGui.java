package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.sakurawald.module.initializer.head.privoder.HeadProvider;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

class SearchInputGui extends AnvilInputGui {

    private final @NotNull HeadGui parentGui;

    public SearchInputGui(@NotNull HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;

        this.setDefaultInputValue("");
        this.setSlot(1, Items.BARRIER.getDefaultStack());
        this.setSlot(2, new GuiElementBuilder().setItem(Items.SLIME_BALL).setName(MessageHelper.ofText(player, "confirm")).setCallback((index, type, action, gui) -> {
            String search = this.getInput();
            var heads = HeadProvider.getHeads().values().stream()
                    .filter(head -> head.name.toLowerCase().contains(search.toLowerCase())
                            || head.getTagsOrEmpty().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());

            var $gui = new PagedHeadsGui(this, heads);
            $gui.setTitle(MessageHelper.ofText(player, "head.search.output", search));
            $gui.open();
        }));
    }

    @Override
    public void onClose() {
        parentGui.open();
    }
}
