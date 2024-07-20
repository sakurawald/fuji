package io.github.sakurawald.module.initializer.kit.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import io.github.sakurawald.module.common.gui.PagedGui;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Random;

@Slf4j
public class KitEditorGui extends PagedGui<Integer> {

    public KitEditorGui(ServerPlayerEntity player, List<Integer> entities) {
        super(player, MessageUtil.ofText(player, true, "kit.gui.editor.title"), entities);
    }


    @Override
    public void onConstructor(PagedGui<Integer> parent) {

    }

    @Override
    public GuiElementInterface toGuiElement(Integer entity) {

        return new GuiElementBuilder().setItem(Items.APPLE)
                .setName(Text.literal(String.valueOf(entity)))
                .build();
    }

    @Override
    public List<Integer> filter(String keyword) {
        return getEntities().stream().filter(e -> e.toString().contains(keyword)).toList();
    }

}
