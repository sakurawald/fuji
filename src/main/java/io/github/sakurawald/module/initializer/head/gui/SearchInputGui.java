package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.head.HeadModule;
import io.github.sakurawald.util.MessageUtil;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.item.Items;

class SearchInputGui extends AnvilInputGui {
    final HeadModule module = ModuleManager.getInitializer(HeadModule.class);
    private final HeadGui parentGui;

    public SearchInputGui(HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;

        this.setDefaultInputValue("");
        this.setSlot(1, Items.BARRIER.getDefaultStack());
        this.setSlot(2, new GuiElementBuilder().setItem(Items.SLIME_BALL).setName(MessageUtil.ofVomponent(player, "confirm")).setCallback((index, type, action, gui) -> {

            String search = this.getInput();
            var heads = module.heads.values().stream()
                    .filter(head -> head.name.toLowerCase().contains(search.toLowerCase()) || head.getTagsOrEmpty().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            var $gui = new PagedHeadsGui(this, heads);
            $gui.setTitle(MessageUtil.ofVomponent(player, "head.search.output", search));
            $gui.open();
        }));
    }

    @Override
    public void onClose() {
        parentGui.open();
    }
}
