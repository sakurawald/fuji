package io.github.sakurawald.module.initializer.fuji.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.structure.CommandDescriptor;
import io.github.sakurawald.core.gui.PagedGui;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandDescriptorGui extends PagedGui<CommandDescriptor> {
    public CommandDescriptorGui(ServerPlayerEntity player, @NotNull List<CommandDescriptor> entities, int pageIndex) {
        super(null, player, LocaleHelper.getTextByKey(player, "fuji.inspect.fuji_commands.gui.title"), entities, pageIndex);
    }

    @Override
    public PagedGui<CommandDescriptor> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<CommandDescriptor> entities, int pageIndex) {
        return new CommandDescriptorGui(player, entities, pageIndex);
    }

    @Override
    public GuiElementInterface toGuiElement(CommandDescriptor entity) {
        return new GuiElementBuilder()
            .setName(Text.literal(entity.computeCommandSyntax()))
            .setLore(List.of(
                LocaleHelper.getTextByKey(getPlayer(), "command.source.can_be_executed_by_console", entity.canBeExecutedByConsole())
                , LocaleHelper.getTextByKey(getPlayer(), "command.descriptor.type", entity.getClass().getSimpleName())
                , LocaleHelper.getTextByKey(getPlayer(), "command.requirement.level_permission", entity.computeLevelPermission())
                , LocaleHelper.getTextByKey(getPlayer(), "command.requirement.string_permission", entity.computeStringPermission())
            ))
            .setItem(Items.REPEATING_COMMAND_BLOCK)
            .build();
    }

    @Override
    public List<CommandDescriptor> filter(String keyword) {
        return getEntities().stream()
            .filter(it -> it.toString().contains(keyword)).toList();
    }
}
