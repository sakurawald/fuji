package fun.sakurawald.module.head.gui;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import fun.sakurawald.module.head.HeadModule;
import net.minecraft.world.item.Items;

import java.util.stream.Collectors;

import static fun.sakurawald.util.MessageUtil.ofVomponent;

class SearchInputGui extends AnvilInputGui {
    private final HeadGui parentGui;

    public SearchInputGui(HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;

        this.setDefaultInputValue("");
        this.setSlot(1, Items.BARRIER.getDefaultInstance());
        this.setSlot(2, Items.SLIME_BALL.getDefaultInstance().setHoverName(ofVomponent(player, "confirm")), (index, type, action, gui) -> {
            String search = this.getInput();
            var heads = HeadModule.heads.values().stream()
                    .filter(head -> head.name.toLowerCase().contains(search.toLowerCase()) || head.getTagsOrEmpty().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
            var $gui = new PagedHeadsGui(this, heads);
            $gui.setTitle(ofVomponent(player, "head.search.output", search));
            $gui.open();
        });
    }

    @Override
    public void onClose() {
        parentGui.open();
    }
}
