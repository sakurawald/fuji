package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.model.WorksModel;
import io.github.sakurawald.module.common.gui.InputSignGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.work_type.NonProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.ProductionWork;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class AddWorkGui extends InputSignGui {


    public AddWorkGui(ServerPlayerEntity player) {
        super(player, MessageUtil.getString(player, "works.work.add.prompt.input.name"));
    }

    @Override
    public void onClose() {
        /* input name */
        String name = this.getLine(0).getString().trim();
        if (name.isBlank()) {
            MessageUtil.sendActionBar(player, "works.work.add.empty_name");
            return;
        }

        /* input type */
        SimpleGui selectWorkTypeGui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        selectWorkTypeGui.setTitle(MessageUtil.ofText(player, "works.work.add.select_work_type.title"));
        GuiUtil.fill(selectWorkTypeGui,GuiUtil.Item.PLACEHOLDER);

        ConfigHandler<WorksModel> worksHandler = WorksInitializer.worksHandler;
        selectWorkTypeGui.setSlot(11, new GuiElementBuilder().setItem(Items.GUNPOWDER).setName(MessageUtil.ofText(player, "works.non_production_work.name")).setCallback(() -> {
            // add
            worksHandler.model().works.addFirst(new NonProductionWork(player, name));
            MessageUtil.sendActionBar(player, "works.work.add.done");
            MessageUtil.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();
        }));

        selectWorkTypeGui.setSlot(15, new GuiElementBuilder().setItem(Items.REDSTONE).setName(MessageUtil.ofText(player, "works.production_work.name")).setCallback(() -> {
            // add
            ProductionWork work = new ProductionWork(player, name);
            worksHandler.model().works.addFirst(work);
            MessageUtil.sendActionBar(player, "works.work.add.done");
            MessageUtil.sendBroadcast("works.work.add.broadcast", player.getGameProfile().getName(), name);
            selectWorkTypeGui.close();

            // input sample distance
            work.openInputSampleDistanceGui(player);
        }));
        selectWorkTypeGui.open();
    }

}
