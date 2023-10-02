package fun.sakurawald.module.head.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import fun.sakurawald.module.head.HeadModule;
import fun.sakurawald.module.head.api.Category;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

import static fun.sakurawald.util.MessageUtil.ofVomponent;

public class HeadGui extends SimpleGui {
    protected final ServerPlayer player;

    public HeadGui(ServerPlayer player) {
        super(MenuType.GENERIC_9x2, player, false);

        this.player = player;

        int index = 0;
        for (Category category : Category.values()) {
            addCategoryButton(index, category);
            ++index;
        }
        this.setTitle(ofVomponent(player, "head.title"));
        this.setSlot(this.getSize() - 1, new GuiElementBuilder()
                .setItem(Items.COMPASS)
                .setName(ofVomponent(player, "search"))
                .setCallback((index1, type1, action) -> new SearchInputGui(this).open()));
        this.setSlot(this.getSize() - 2, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(ofVomponent(player, "head.category.player"))
                .setCallback((index1, type1, action) -> new PlayerInputGui(this).open()));
    }

    private void addCategoryButton(int index, Category category) {
        this.setSlot(index, category.of(player), (i, type, action, gui) -> {
            var headsGui = new PagedHeadsGui(this, new ArrayList<>(HeadModule.heads.get(category)));
            headsGui.setTitle(category.getDisplayName(player));
            headsGui.open();
        });
    }

}
