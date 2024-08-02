package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.module.initializer.works.model.WorksModel;
import io.github.sakurawald.module.common.gui.InputSignGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.work_type.NonProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.ProductionWork;
import io.github.sakurawald.util.minecraft.GuiHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
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
            MessageHelper.sendActionBar(player, "works.work.add.empty_name");
            return;
        }

        /* input type */
        SimpleGui selectWorkTypeGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        selectWorkTypeGui.setTitle(MessageHelper.ofText(player, "works.work.add.select_work_type.title"));
        GuiHelper.fill(selectWorkTypeGui, GuiHelper.Item.PLACEHOLDER);

        ConfigHandler<WorksModel> worksHandler = WorksInitializer.worksHandler;
        selectWorkTypeGui.setSlot(11, new GuiElementBuilder().setItem(Items.GUNPOWDER).setName(MessageHelper.ofText(player, "works.non_production_work.name")).setCallback(() -> {
            // add
            worksHandler.model().works.addFirst(new NonProductionWork(player, name));
            MessageHelper.sendActionBar(player, "works.work.add.done");
            MessageHelper.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();
        }));

        selectWorkTypeGui.setSlot(15, new GuiElementBuilder().setItem(Items.REDSTONE).setName(MessageHelper.ofText(player, "works.production_work.name")).setCallback(() -> {
            // add
            ProductionWork work = new ProductionWork(player, name);
            worksHandler.model().works.addFirst(work);
            MessageHelper.sendActionBar(player, "works.work.add.done");
            MessageHelper.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();

            // input sample distance
            work.openInputSampleDistanceGui(player);
        }));
        selectWorkTypeGui.open();
    }

}
