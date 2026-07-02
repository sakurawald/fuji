package mod.fuji.core.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import mod.fuji.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import mod.fuji.core.command.argument.structure.CommandArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.ChatFormatting;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class TeamColorArgumentTypeAdapter extends BaseArgumentTypeAdapter {
    @Override
    protected ArgumentType<?> makeArgumentType() {
        #if MC_VER < MC_26_2
        return net.minecraft.commands.arguments.ColorArgument.color();
        #elif MC_VER >= MC_26_2
        return net.minecraft.commands.arguments.TeamColorArgument.teamColor();
        #endif
    }

    @Override
    protected Object makeArgumentValue(@NotNull CommandContext<CommandSourceStack> context, @NotNull CommandArgument commandArgument) {
        #if MC_VER < MC_26_2
        return net.minecraft.commands.arguments.ColorArgument.getColor(context, commandArgument.getArgumentName());
        #elif MC_VER >= MC_26_2
        return net.minecraft.commands.arguments.TeamColorArgument.getTeamColor(context, commandArgument.getArgumentName());
        #endif
    }

    @Override
    public List<Class<?>> getTypeClasses() {
        return List.of(ChatFormatting.class);
    }

    @Override
    public List<String> getTypeNames() {
        return List.of("color", "team-color");
    }
}
