package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.layered.Layer;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.gui.PagedGui;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.structure.work.abst.Work;
import io.github.sakurawald.module.initializer.works.structure.work.impl.ProductionWork;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorksGui extends PagedGui<Work> {

    public WorksGui(ServerPlayerEntity player, @NotNull List<Work> entities, int pageIndex) {
        super(null, player, LocaleHelper.getTextByKey(player, "works.list.title"), entities, pageIndex);

        Layer controlLayer = new Layer(1, 3);
        controlLayer.addSlot(GuiHelper.makeAddButton(player)
            .setName(LocaleHelper.getTextByKey(player, "works.list.add"))
            .setCallback(() -> new AddWorkGui(player).open())
        );
        controlLayer.addSlot(new GuiElementBuilder()
            .setItem(Items.PLAYER_HEAD)
            .setName(LocaleHelper.getTextByKey(player, "help"))
            .setSkullOwner(GuiHelper.Icon.QUESTION_MARK_ICON)
            .setLore(LocaleHelper.getTextListByKey(player, "works.list.help.lore")));

        if (entities == WorksInitializer.worksHandler.getModel().works) {
            controlLayer.addSlot(GuiHelper.makeHelpButton(player)
                .setName(LocaleHelper.getTextByKey(player, "works.list.my_works"))
                .setCallback(() -> search(player.getGameProfile().getName()).open())
            );
        } else {
            controlLayer.addSlot(new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(LocaleHelper.getTextByKey(player, "works.list.all_works"))
                .setSkullOwner(GuiHelper.Icon.A_ICON)
                .setCallback(() -> new WorksGui(player, WorksInitializer.worksHandler.getModel().works, 0).open())
            );
        }

        // note: in this closure, it's important to call `the.addLayer`, not `this.addLayer() or super.addLayer()`
        this.addLayer(controlLayer, 3, this.getHeight() - 1);
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasPermission(@NotNull ServerPlayerEntity player, @NotNull Work work) {
        return player.getGameProfile().getName().equals(work.creator) || player.hasPermissionLevel(4);
    }

    @Override
    public PagedGui<Work> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<Work> entities, int pageIndex) {
        return new WorksGui(player, entities, 0);
    }

    @Override
    public GuiElementInterface toGuiElement(@NotNull Work entity) {
        ServerPlayerEntity player = getPlayer();
        return new GuiElementBuilder()
            .setItem(entity.asItem())
            .setName(LocaleHelper.getTextByValue(null,entity.name))
            .setLore(entity.asLore(player))
            .setCallback((index, clickType, actionType) -> {
                /* left click -> visit */
                if (clickType.isLeft) {
                    RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(entity.level));
                    ServerWorld level = ServerHelper.getDefaultServer().getWorld(worldKey);
                    if (level != null) {
                        player.teleport(level, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
                    } else {
                        LocaleHelper.sendMessageByKey(player, "world.dimension.not_found");
                    }

                    this.close();
                    return;
                }
                /* shift + right click -> specialized settings */
                if (clickType.isRight && clickType.shift) {
                    if (!hasPermission(player, entity)) {
                        LocaleHelper.sendActionBarByKey(player, "works.work.set.no_perm");
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
                        LocaleHelper.sendActionBarByKey(player, "works.work.set.no_perm");
                        return;
                    }
                    entity.openGeneralSettingsGui(player, gui);
                    this.close();
                }
            }).build();
    }

    @Override
    public @NotNull List<Work> filter(@NotNull String keyword) {
        return this.getEntities().stream().filter(w ->
            w.creator.contains(keyword)
                || w.name.contains(keyword)
                || (w.introduction != null && w.introduction.contains(keyword))
                || w.level.contains(keyword)
                || w.getIcon().contains(keyword)
                || (w instanceof ProductionWork pw && pw.sample.sampleCounter != null && pw.sample.sampleCounter.keySet().stream().anyMatch(k -> k.contains(keyword)))
        ).toList();
    }
}
