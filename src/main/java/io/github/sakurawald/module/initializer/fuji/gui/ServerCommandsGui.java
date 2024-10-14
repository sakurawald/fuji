package io.github.sakurawald.module.initializer.fuji.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.gui.PagedGui;
import io.github.sakurawald.core.gui.layer.SingleLineLayer;
import io.github.sakurawald.core.structure.CommandNode;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ServerCommandsGui extends PagedGui<CommandNode> {

    public ServerCommandsGui(ServerPlayerEntity player, @NotNull List<CommandNode> entities, int pageIndex) {
        super(null, player, LocaleHelper.getTextByKey(player, "fuji.inspect.server_commands.gui.title"), entities, pageIndex);

        SingleLineLayer footer = new SingleLineLayer();
        footer.setSlot(4, GuiHelper.makeHelpButton(player)
            .setLore(LocaleHelper.getTextListByKey(player, "fuji.inspect.server_commands.gui.help.lore")));
        this.addLayer(footer, 0, this.getHeight() - 1);
    }

    @Override
    public PagedGui<CommandNode> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<CommandNode> entities, int pageIndex) {
        return new ServerCommandsGui(player, entities, pageIndex);
    }

    @Override
    public GuiElementInterface toGuiElement(CommandNode entity) {
        return new GuiElementBuilder()
            .setItem(Items.COMMAND_BLOCK)
            .setName(Text.literal(entity.getPath()))
            .setCallback((index, clickType, actionType) -> {
                String commandPath = entity.getPath();
                if (clickType.isLeft) {
                    LocaleHelper.sendMessageByKey(getPlayer(), "fuji.inspect.server_commands.gui.copy_command_path", commandPath, commandPath);
                }
                close();
            })
            .build();
    }

    @Override
    public List<CommandNode> filter(String keyword) {
        return getEntities().stream()
            .filter(it -> it.getPath().contains(keyword))
            .toList();
    }
}
