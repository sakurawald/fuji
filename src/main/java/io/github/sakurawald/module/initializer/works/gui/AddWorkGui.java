package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.gui.InputSignGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.config.model.WorksDataModel;
import io.github.sakurawald.module.initializer.works.structure.work.impl.NonProductionWork;
import io.github.sakurawald.module.initializer.works.structure.work.impl.ProductionWork;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class AddWorkGui extends InputSignGui {


    public AddWorkGui(@NotNull ServerPlayerEntity player) {
        super(player,LocaleHelper.getTextByKey(player, "works.work.add.prompt.input.name"));
    }

    @Override
    public void onClose() {
        /* input name */
        String name = this.getLine(0).getString().trim();
        if (name.isBlank()) {
            LocaleHelper.sendActionBarByKey(player, "works.work.add.empty_name");
            return;
        }

        /* input type */
        SimpleGui selectWorkTypeGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        selectWorkTypeGui.setTitle(LocaleHelper.getTextByKey(player, "works.work.add.select_work_type.title"));
        GuiHelper.fill(selectWorkTypeGui, GuiHelper.Item.PLACEHOLDER);

        BaseConfigurationHandler<WorksDataModel> worksHandler = WorksInitializer.worksHandler;
        selectWorkTypeGui.setSlot(11, new GuiElementBuilder().setItem(Items.GUNPOWDER).setName(LocaleHelper.getTextByKey(player, "works.non_production_work.name")).setCallback(() -> {
            // add
            worksHandler.getModel().works.addFirst(new NonProductionWork(player, name));
            LocaleHelper.sendActionBarByKey(player, "works.work.add.done");
            LocaleHelper.sendBroadcastByKey("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();
        }));

        selectWorkTypeGui.setSlot(15, new GuiElementBuilder().setItem(Items.REDSTONE).setName(LocaleHelper.getTextByKey(player, "works.production_work.name")).setCallback(() -> {
            // add
            ProductionWork work = new ProductionWork(player, name);
            worksHandler.getModel().works.addFirst(work);
            LocaleHelper.sendActionBarByKey(player, "works.work.add.done");
            LocaleHelper.sendBroadcastByKey("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();

            // input sample distance
            work.openInputSampleDistanceGui(player);
        }));
        selectWorkTypeGui.open();
    }

}
