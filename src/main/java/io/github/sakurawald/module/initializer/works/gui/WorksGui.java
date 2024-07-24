package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import io.github.sakurawald.module.common.gui.PagedGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.work_type.ProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.minecraft.GuiHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class WorksGui extends PagedGui<Work> {

    public WorksGui(ServerPlayerEntity player, List<Work> entities) {
        super(player, MessageHelper.ofText(player, "works.list.title"), entities);
    }

    @Override
    public void onConstructor(PagedGui<Work> the) {
        ServerPlayerEntity player = getPlayer();
        List<Work> entities = the.getEntities();

        Layer controlLayer = new Layer(1, 3);
        controlLayer.addSlot(GuiHelper.createAddButton(player)
                .setName(MessageHelper.ofText(player, "works.list.add"))
                .setCallback(() -> new AddWorkGui(player).open())
        );
        controlLayer.addSlot(new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageHelper.ofText(player, "works.list.help"))
                .setSkullOwner(GuiHelper.Icon.QUESTION_MARK_ICON)
                .setLore(MessageHelper.ofTextList(player, "works.list.help.lore")));

        if (entities == WorksInitializer.worksHandler.model().works) {
            controlLayer.addSlot(GuiHelper.createHelpButton(player)
                    .setName(MessageHelper.ofText(player, "works.list.my_works"))
                    .setCallback(() -> search(player.getGameProfile().getName()).open())
            );
        } else {
            controlLayer.addSlot(new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(MessageHelper.ofText(player, "works.list.all_works"))
                    .setSkullOwner(GuiHelper.Icon.A_ICON)
                    .setCallback(() -> new WorksGui(player, WorksInitializer.worksHandler.model().works).open())
            );
        }

        // note: in this closure, it's important to call `the.addLayer`, not `this.addLayer() or super.addLayer()`
        the.addLayer(controlLayer, 3, the.getHeight() - 1);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasPermission(ServerPlayerEntity player, Work work) {
        return player.getGameProfile().getName().equals(work.creator) || player.hasPermissionLevel(4);
    }

    @Override
    public GuiElementInterface toGuiElement(PagedGui<Work> the, Work entity) {
        ServerPlayerEntity player = getPlayer();
        return new GuiElementBuilder()
                .setItem(entity.asItem())
                .setName(MessageHelper.ofText(entity.name))
                .setLore(entity.asLore(player))
                .setCallback((index, clickType, actionType) -> {
                    /* left click -> visit */
                    if (clickType.isLeft) {
                        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(entity.level));
                        ServerWorld level = ServerHelper.getDefaultServer().getWorld(worldKey);
                        player.teleport(level, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
                        the.close();
                        return;
                    }
                    /* shift + right click -> specialized settings */
                    if (clickType.isRight && clickType.shift) {
                        if (!hasPermission(player, entity)) {
                            MessageHelper.sendActionBar(player, "works.work.set.no_perm");
                            return;
                        }
                        entity.openSpecializedSettingsGui(player, gui);
                        the.close();
                        return;
                    }
                    /* right click -> general settings */
                    if (clickType.isRight) {
                        // check permission
                        if (!hasPermission(player, entity)) {
                            MessageHelper.sendActionBar(player, "works.work.set.no_perm");
                            return;
                        }
                        entity.openGeneralSettingsGui(player, gui);
                        the.close();
                    }
                }).build();
    }

    @Override
    public List<Work> filter(String keyword) {
        return getThis().getEntities().stream().filter(w ->
                w.creator.contains(keyword)
                        || w.name.contains(keyword)
                        || (w.introduction != null && w.introduction.contains(keyword))
                        || w.level.contains(keyword)
                        || w.getIcon().contains(keyword)
                        || (w instanceof ProductionWork pw && pw.sample.sampleCounter != null && pw.sample.sampleCounter.keySet().stream().anyMatch(k -> k.contains(keyword)))
        ).toList();
    }
}
