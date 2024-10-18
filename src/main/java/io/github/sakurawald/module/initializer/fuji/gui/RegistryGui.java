package io.github.sakurawald.module.initializer.fuji.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.gui.PagedGui;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegistryGui extends PagedGui<Identifier> {

    private final Registry<?> registry;

    public RegistryGui(@Nullable SimpleGui parent, ServerPlayerEntity player, Registry<?> registry, @NotNull List<Identifier> entities, int pageIndex) {
        super(parent, player, LocaleHelper.getTextByKey(player, "registry.list.gui.title"), entities, pageIndex);

        // re-draw since it is still null
        this.registry = registry;
        drawPagedGui();
    }

    @Override
    public PagedGui<Identifier> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<Identifier> entities, int pageIndex) {
        return new RegistryGui(parent, player, registry, entities, pageIndex);
    }

    @Override
    public GuiElementInterface toGuiElement(Identifier entity) {

        return new GuiElementBuilder()
            .setName(Text.of(entity.toString()))
            .setItem(isMetaRegistry() ? Items.BOOKSHELF : Items.PAPER)
            .setCallback(() -> {
                if (!isMetaRegistry()) return;

                Object o = this.registry.get(entity);

                if (o instanceof Registry<?> r) {
                    List<Identifier> list = r.getKeys().stream()
                        .map(RegistryKey::getValue)
                        .sorted()
                        .toList();
                    new RegistryGui(getGui(), getPlayer(), r, list, 0).open();
                }

            })
            .build();
    }

    private boolean isMetaRegistry() {
        return Registries.REGISTRIES.equals(this.registry);
    }

    @Override
    public List<Identifier> filter(String keyword) {
        return getEntities()
            .stream()
            .filter(it -> it.toString().contains(keyword))
            .toList();
    }
}
