package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.gui.InputSignGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.config.model.WorksModel;
import io.github.sakurawald.module.initializer.works.structure.work.impl.NonProductionWork;
import io.github.sakurawald.module.initializer.works.structure.work.impl.ProductionWork;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class AddWorkGui extends InputSignGui {


    public AddWorkGui(@NotNull ServerPlayerEntity player) {
        super(player, "works.work.add.prompt.input.name");
    }

    @Override
    public void onClose() {
        /* input name */
        String name = this.getLine(0).getString().trim();
        if (name.isBlank()) {
            MessageHelper.sendActionBarByKey(player, "works.work.add.empty_name");
            return;
        }

        /* input type */
        SimpleGui selectWorkTypeGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        selectWorkTypeGui.setTitle(MessageHelper.getTextByKey(player, "works.work.add.select_work_type.title"));
        GuiHelper.fill(selectWorkTypeGui, GuiHelper.Item.PLACEHOLDER);

        ConfigHandler<WorksModel> worksHandler = WorksInitializer.worksHandler;
        selectWorkTypeGui.setSlot(11, new GuiElementBuilder().setItem(Items.GUNPOWDER).setName(MessageHelper.getTextByKey(player, "works.non_production_work.name")).setCallback(() -> {
            // add
            worksHandler.model().works.addFirst(new NonProductionWork(player, name));
            MessageHelper.sendActionBarByKey(player, "works.work.add.done");
            MessageHelper.sendBroadcastByKey("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();
        }));

        selectWorkTypeGui.setSlot(15, new GuiElementBuilder().setItem(Items.REDSTONE).setName(MessageHelper.getTextByKey(player, "works.production_work.name")).setCallback(() -> {
            // add
            ProductionWork work = new ProductionWork(player, name);
            worksHandler.model().works.addFirst(work);
            MessageHelper.sendActionBarByKey(player, "works.work.add.done");
            MessageHelper.sendBroadcastByKey("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();

            // input sample distance
            work.openInputSampleDistanceGui(player);
        }));
        selectWorkTypeGui.open();
    }

}
