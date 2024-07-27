package io.github.sakurawald.module.initializer.head.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.sakurawald.module.common.gui.InputSignGui;
import io.github.sakurawald.module.initializer.head.privoder.HeadProvider;
import io.github.sakurawald.module.initializer.head.structure.Head;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class SearchInputGui extends InputSignGui {

    private final @NotNull HeadGui parentGui;

    public SearchInputGui(@NotNull HeadGui parentGui) {
        super(parentGui.player);
        this.parentGui = parentGui;
    }

    @Override
    public void onClose() {
        String keywords = reduceInputOrEmpty();

        if (keywords.isEmpty()) {
            parentGui.open();
            return;
        }

        List<Head> entities = HeadProvider.getHeads().values().stream()
                .filter(head -> head.name.toLowerCase().contains(keywords.toLowerCase())
                        || head.getTagsOrEmpty().toLowerCase().contains(keywords.toLowerCase()))
                .collect(Collectors.toList());
        Text title = MessageHelper.ofText(player, "head.search.output", keywords);
        new MyPagedHeadsGui(this.parentGui, player, title, entities, 0).open();
    }
}
