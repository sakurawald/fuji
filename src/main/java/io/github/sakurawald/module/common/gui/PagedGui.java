package io.github.sakurawald.module.common.gui;

import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.layered.LayeredGui;
import io.github.sakurawald.module.common.gui.layer.SingleLineLayer;
import io.github.sakurawald.util.minecraft.GuiHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PagedGui<T> extends LayeredGui {

    @Getter
    private final List<T> entities;
    private final int pageIndex;
    private final Text title;

    public PagedGui(ServerPlayerEntity player, Text title, @NotNull List<T> entities) {
        this(player, title, entities, 0);
    }

    public PagedGui(ServerPlayerEntity player, Text title, @NotNull List<T> entities, int pageIndex) {
        super(ScreenHandlerType.GENERIC_9X6, player, false);
        layers.clear();

        // props
        this.title = title;
        this.pageIndex = pageIndex;
        this.entities = entities;

        // draw title
        this.drawTitle();

        // draw entities
        int slotIndex = 0;
        for (int i = getEntityBeginIndex(this.pageIndex); i < getEntityEndIndex(this.pageIndex); i++) {
            T entity = entities.get(i);
            this.setSlot(slotIndex++, toGuiElement(this, entity));
        }

        // page layer
        SingleLineLayer pageLayer = new SingleLineLayer(GuiHelper.createPlaceholder());
        pageLayer.setSlot(0, GuiHelper.createPreviousPageButton(player).setCallback(() -> tryChangePage(pageIndex - 1)));
        pageLayer.setSlot(this.getWidth() - 1, GuiHelper.createPreviousPageButton(player).setCallback(() -> tryChangePage(pageIndex + 1)));
        pageLayer.setSlot(this.getWidth() - 2, GuiHelper.createSearchButton(player).setCallback(() -> new InputSignGui(player, null) {
            @Override
            public void onClose() {
                String keyword = reduceInputOrEmpty();
                search(keyword).open();
            }
        }.open()));
        this.addLayer(pageLayer, 0, this.getHeight() - 1);

        // events
        onConstructor(this);
    }

    private void tryChangePage(int newPageIndex) {
        int entityBeginIndex = getEntityBeginIndex(newPageIndex);
        if (entityBeginIndex < 0 || entityBeginIndex >= getEntitySize()) return;

        of(this.entities, newPageIndex).open();
    }

    protected @NotNull PagedGui<T> search(String keyword) {
        return of(filter(keyword), 0);
    }

    protected void addEneity(T entity) {
        this.entities.add(entity);
        this.reopen();
    }

    protected void deleteEntity(T entity) {
        this.entities.remove(entity);
        this.reopen();
    }

    protected void reopen() {
        this.of(this.entities, pageIndex).open();
    }

    private @NotNull PagedGui<T> of(@NotNull List<T> entities, int pageIndex) {
        PagedGui<T> that = this;
        return new PagedGui<>(getPlayer(), that.title, entities, pageIndex) {

            @Override
            public void onConstructor(PagedGui<T> the) {
                that.onConstructor(the);
            }

            @Override
            public GuiElementInterface toGuiElement(PagedGui<T> the, T entity) {
                return that.toGuiElement(this, entity);
            }

            @Override
            public List<T> filter(String keyword) {
                return that.filter(keyword);
            }

            /**
             * used for dynamic binding of click-callback
             */
            @Override
            public @NotNull PagedGui<T> getThis() {
                return this;
            }
        };
    }

    public @NotNull PagedGui<T> getThis() {
        return this;
    }

    private void drawTitle() {
        Component formatted = this.title.asComponent()
                .append(MessageHelper.ofText(getPlayer(), true, "gui.page.title", this.getCurrentPageNumber(), this.getMaxPageNumber()));
        this.setTitle(MessageHelper.toText(formatted));
    }

    private int getEntitySize() {
        return this.entities.size();
    }

    private int getCurrentPageNumber() {
        return this.pageIndex + 1;
    }

    private int getMaxPageNumber() {
        int a = this.getEntitySize();
        int b = this.getEntityPageSize();
        int bias = 0;
        if (a % b != 0) bias = 1;
        return a / b + bias;
    }

    private int getEntityPageSize() {
        return (this.getWidth() * this.getHeight()) - 9;
    }

    private int getEntityBeginIndex(int pageIndex) {
        return this.getEntityPageSize() * pageIndex;
    }

    private int getEntityEndIndex(int pageIndex) {
        return Math.min(getEntityBeginIndex(pageIndex + 1), this.getEntitySize());
    }

    public abstract void onConstructor(PagedGui<T> parent);

    public abstract GuiElementInterface toGuiElement(PagedGui<T> ref, T entity);

    public abstract List<T> filter(String keyword);
}
