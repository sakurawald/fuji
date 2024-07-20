package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.layered.Layer;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.common.gui.PagedGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.work_type.ProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

@Slf4j
public class WorksGui extends PagedGui<Work> {

    public WorksGui(ServerPlayerEntity player, List<Work> entities) {
        super(player, MessageUtil.ofText(player, "works.list.title"), entities);
    }

    @Override
    public void onConstructor(PagedGui<Work> parent) {
        ServerPlayerEntity player = getPlayer();
        List<Work> entities = parent.getEntities();

        Layer controlLayer = new Layer(1, 3);
        controlLayer.addSlot(GuiUtil.createAddButton(player)
                .setName(MessageUtil.ofText(player, "works.list.add"))
                .setCallback(() -> new AddWorkGui(player).open())
        );
        controlLayer.addSlot(new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofText(player, "works.list.help"))
                .setSkullOwner(GuiUtil.Icon.QUESTION_MARK_ICON)
                .setLore(MessageUtil.ofTextList(player, "works.list.help.lore")));

        if (entities == WorksInitializer.worksHandler.model().works) {
            controlLayer.addSlot(GuiUtil.createHelpButton(player)
                    .setName(MessageUtil.ofText(player, "works.list.my_works"))
                    .setCallback(() -> search(player.getGameProfile().getName()).open())
            );
        } else {
            controlLayer.addSlot(new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(MessageUtil.ofText(player, "works.list.all_works"))
                    .setSkullOwner(GuiUtil.Icon.A_ICON)
                    .setCallback(() -> new WorksGui(player, WorksInitializer.worksHandler.model().works).open())
            );
        }

        // note: in this closure, it's important to call `parent.addLayer`, not `this.addLayer() or super.addLayer()`
        parent.addLayer(controlLayer, 3, this.getHeight() - 1);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasPermission(ServerPlayerEntity player, Work work) {
        return player.getGameProfile().getName().equals(work.creator) || player.hasPermissionLevel(4);
    }

    @Override
    public GuiElementInterface toGuiElement(Work entity) {
        ServerPlayerEntity player = getPlayer();
        return new GuiElementBuilder()
                .setItem(entity.asItem())
                .setName(MessageUtil.ofText(entity.name))
                .setLore(entity.asLore(player))
                .setCallback((index, clickType, actionType) -> {
                    /* left click -> visit */
                    if (clickType.isLeft) {
                        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(entity.level));
                        ServerWorld level = Fuji.SERVER.getWorld(worldKey);
                        player.teleport(level, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
                        this.close();
                        return;
                    }
                    /* shift + right click -> specialized settings */
                    if (clickType.isRight && clickType.shift) {
                        if (!hasPermission(player, entity)) {
                            MessageUtil.sendActionBar(player, "works.work.set.no_perm");
                            return;
                        }
                        entity.openSpecializedSettingsGui(player, gui);
                        this.close();
                        return;
                    }
                    /* right click -> general settings */
                    if (clickType.isRight) {
                        // check permission
                        if (!hasPermission(player, entity)) {
                            MessageUtil.sendActionBar(player, "works.work.set.no_perm");
                            return;
                        }
                        entity.openGeneralSettingsGui(player, gui);
                        this.close();
                    }
                }).build();
    }

    @Override
    public List<Work> filter(String keyword) {
        return getEntities().stream().filter(w ->
                w.creator.contains(keyword)
                        || w.name.contains(keyword)
                        || (w.introduction != null && w.introduction.contains(keyword))
                        || w.level.contains(keyword)
                        || w.getIcon().contains(keyword)
                        || (w instanceof ProductionWork pw && pw.sample.sampleCounter != null && pw.sample.sampleCounter.keySet().stream().anyMatch(k -> k.contains(keyword)))
        ).toList();
    }
}
