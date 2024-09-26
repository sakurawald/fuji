package io.github.sakurawald.module.initializer.fuji.gui;

import com.mojang.authlib.GameProfile;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.GuiHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.gui.PagedGui;
import io.github.sakurawald.core.gui.layer.SingleLineLayer;
import io.github.sakurawald.core.service.gameprofile_fetcher.MojangProfileFetcher;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AboutGui extends PagedGui<Person> {

    public AboutGui(ServerPlayerEntity player, @NotNull List<Person> entities, int pageIndex) {
        super(null, player, LocaleHelper.getTextByKey(player, "about"), entities, pageIndex);

        ModMetadata metadata = FabricLoader.getInstance().getModContainer(Fuji.MOD_ID).get().getMetadata();
        SingleLineLayer footer = new SingleLineLayer();
        footer.setSlot(1, new GuiElementBuilder(Items.BOOK)
            .setName(LocaleHelper.getTextByKey(player, "version.format", metadata.getVersion().getFriendlyString())));
        footer.setSlot(4, new GuiElementBuilder(Items.NETHER_STAR)
            .setName(LocaleHelper.getTextByKey(player, "homepage.project"))
            .setCallback(() -> {
                LocaleHelper.sendMessageByKey(player, "homepage.project.visit", metadata.getContact().get("sources").orElse("can't read project homepage from metadata"));
                this.close();
            }));
        this.addLayer(footer, 0, this.getHeight() - 1);

        // fetch heads async
        fetchHeads();
    }

    private void fetchHeads() {
        for (int i = 0; i < 54; i++) {
            GuiElementInterface slot = this.getSlot(i);
            if (slot == null) return;

            /* run async for each head */
            int finalI = i;
            CompletableFuture.runAsync(() -> {
                ItemStack itemStack = slot.getItemStack();

                // get the player name from the item name
                String onlinePlayerName = itemStack.getName().getString().trim();
                GameProfile gameProfile = MojangProfileFetcher.makeGameProfile(onlinePlayerName);

                // with gui slot
                GuiElementBuilder builder = new GuiElementBuilder()
                    .setItem(itemStack.getItem())
                    .setName(itemStack.getName())
                    .setCallback(slot.getGuiCallback())
                    .setSkullOwner(gameProfile, ServerHelper.getDefaultServer());
                LoreComponent loreComponent = itemStack.get(DataComponentTypes.LORE);
                if (loreComponent != null) {
                    builder.setLore(loreComponent.comp_2400());
                }

                setSlot(finalI, builder);

                // draw it
                draw();
            });
        }
    }

    @Override
    public PagedGui<Person> make(@Nullable SimpleGui parent, ServerPlayerEntity player, Text title, @NotNull List<Person> entities, int pageIndex) {
        return new AboutGui(player, entities, pageIndex);
    }

    @SuppressWarnings("HttpUrlsUsage")
    private boolean isUrl(String string) {
        return string.contains("http://") || string.contains("https://");
    }

    public GuiElementInterface.ClickCallback makeCallback(Person entity) {
        return (a, b, c, d) -> {
            // construct the text
            MutableText text = Text.empty();
            text.append(LocaleHelper.getTextByKey(getPlayer(), "contact.visit.name", entity.getName()))
                .append(LocaleHelper.TEXT_NEWLINE);
            entity.getContact().asMap().forEach((k, v) -> text
                .append(LocaleHelper.getTextByKey(getPlayer(), isUrl(v) ? "contact.visit.entry.is_url" : "contact.visit.entry.is_not_url", k, v, v))
                .append(LocaleHelper.TEXT_NEWLINE));

            // send it
            getPlayer().sendMessage(text);
            close();
        };
    }

    public List<Text> makeTextListFromContact(ContactInformation contact) {
        List<Text> ret = new ArrayList<>();
        contact.asMap().forEach((k, v) -> ret.add(LocaleHelper.getTextByKey(getPlayer(), "contact.entry", k, v)));

        // add visit hint lore
        ret.add(LocaleHelper.getTextByKey(getPlayer(), "contact.click.prompt"));
        return ret;
    }

    @Override
    public GuiElementInterface toGuiElement(Person entity) {
        return GuiHelper.makePlayerPlaceholder()
            .setName(LocaleHelper.getTextByKey(getPlayer(), "contact.name", entity.getName()))
            .setLore(makeTextListFromContact(entity.getContact()))
            .setCallback(makeCallback(entity))
            .build();
    }

    @Override
    public List<Person> filter(String keyword) {
        return getEntities().stream().filter(e ->
            {
                Map<String, String> contact = e.getContact().asMap();
                return e.getName().contains(keyword)
                    || contact.entrySet().stream().anyMatch(it -> it.getKey().contains(keyword) || it.getValue().contains(keyword));
            }
        ).toList();
    }
}
