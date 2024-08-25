package io.github.sakurawald.module.initializer.works.structure.work.impl;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.module.initializer.works.structure.work.interfaces.Work;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import lombok.NoArgsConstructor;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class NonProductionWork extends Work {
    public NonProductionWork(@NotNull ServerPlayerEntity player, String name) {
        super(player, name);
    }

    @Override
    protected @NotNull String getType() {
        return WorkTypeAdapter.WorkType.NonProductionWork.name();
    }

    @Override
    protected @NotNull String getDefaultIcon() {
        return "minecraft:gunpowder";
    }

    @Override
    public void openSpecializedSettingsGui(@NotNull ServerPlayerEntity player, SimpleGui parentGui) {
        MessageHelper.sendActionBar(player, "works.non_production_work.specialized_settings.not_found");
    }
}
