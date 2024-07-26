package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.head.HeadInitializer;
import io.github.sakurawald.module.initializer.head.api.Category;
import io.github.sakurawald.util.minecraft.MessageHelper;
import java.util.ArrayList;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class HeadGui extends SimpleGui {
    protected final ServerPlayerEntity player;
    final HeadInitializer module = ModuleManager.getInitializer(HeadInitializer.class);

    public HeadGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X2, player, false);

        this.player = player;

        int index = 0;
        for (Category category : Category.values()) {
            addCategoryButton(index, category);
            ++index;
        }
        this.setTitle(MessageHelper.ofText(player, "head.title"));
        this.setSlot(this.getSize() - 1, new GuiElementBuilder()
                .setItem(Items.COMPASS)
                .setName(MessageHelper.ofText(player, "search"))
                .setCallback((index1, type1, action) -> new SearchInputGui(this).open()));
        this.setSlot(this.getSize() - 2, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageHelper.ofText(player, "head.category.player"))
                .setCallback((index1, type1, action) -> new PlayerInputGui(this).open()));
    }

    private void addCategoryButton(int index, @NotNull Category category) {
        this.setSlot(index, category.of(player), (i, type, action, gui) -> {
            var headsGui = new PagedHeadsGui(this, new ArrayList<>(module.heads.get(category)));
            headsGui.setTitle(category.getDisplayName(player));
            headsGui.open();
        });
    }


}
