package io.github.sakurawald.module.head.gui;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.head.HeadModule;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.world.item.Items;

import java.util.stream.Collectors;

class SearchInputGui extends AnvilInputGui {
    final HeadModule module = ModuleManager.getOrNewInstance(HeadModule.class);
    private final HeadGui parentGui;

    public SearchInputGui(HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;

        this.setDefaultInputValue("");
        this.setSlot(1, Items.BARRIER.getDefaultInstance());
        this.setSlot(2, Items.SLIME_BALL.getDefaultInstance().setHoverName(MessageUtil.ofVomponent(player, "confirm")), (index, type, action, gui) -> {
            String search = this.getInput();
            var heads = module.heads.values().stream()
                    .filter(head -> head.name.toLowerCase().contains(search.toLowerCase()) || head.getTagsOrEmpty().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            var $gui = new PagedHeadsGui(this, heads);
            $gui.setTitle(MessageUtil.ofVomponent(player, "head.search.output", search));
            $gui.open();
        });
    }

    @Override
    public void onClose() {
        parentGui.open();
    }
}
