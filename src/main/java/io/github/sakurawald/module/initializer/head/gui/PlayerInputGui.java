package io.github.sakurawald.module.initializer.head.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.head.HeadInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class PlayerInputGui extends AnvilInputGui {
    final HeadInitializer module = ModuleManager.getInitializer(HeadInitializer.class);
    private final HeadGui parentGui;
    private long apiDebounce = 0;
    private final ItemStack DEFAULT_PLAYER_HEAD = Items.PLAYER_HEAD.getDefaultStack();

    public PlayerInputGui(HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;
        this.setDefaultInputValue("");
        this.setSlot(1, Items.PLAYER_HEAD.getDefaultStack());
        this.resetSearchResult();
        this.setTitle(MessageUtil.ofVomponent(player, "head.category.player"));
    }

    private void resetSearchResult() {
        this.setSlot(2, this.DEFAULT_PLAYER_HEAD);
    }

    @Override
    public void onTick() {
        if (apiDebounce != 0 && apiDebounce <= System.currentTimeMillis()) {
            apiDebounce = 0;

            CompletableFuture.runAsync(() -> {
                MinecraftServer server = player.server;
                UserCache profileCache = server.getUserCache();
                if (profileCache == null) {
                    resetSearchResult();
                    return;
                }

                Optional<GameProfile> possibleProfile = profileCache.findByName(this.getInput());
                MinecraftSessionService sessionService = server.getSessionService();
                if (possibleProfile.isEmpty()) {
                    resetSearchResult();
                    return;
                }

                ProfileResult profileResult = sessionService.fetchProfile(possibleProfile.get().getId(), false);
                if (profileResult == null) {
                    resetSearchResult();
                    return;
                }

                GameProfile profile = profileResult.profile();
                MinecraftProfileTextures textures = sessionService.getTextures(profile);
                if (textures == MinecraftProfileTextures.EMPTY) {
                    resetSearchResult();
                    return;
                }

                GuiElementBuilder builder = new GuiElementBuilder().setItem(Items.PLAYER_HEAD);
                if (HeadInitializer.headHandler.model().economyType != HeadInitializer.EconomyType.FREE) {
                    builder.addLoreLine(Text.empty());
                    builder.addLoreLine(MessageUtil.ofVomponent(player, "head.price").copy().append(module.getCost()));
                }

                builder.setSkullOwner(profile, Fuji.SERVER);

                ItemStack stack = builder.asStack();

                this.setSlot(2, stack, (index, type, action, gui) ->
                        module.tryPurchase(player, 1, () -> {
                            var cursorStack = getPlayer().currentScreenHandler.getCursorStack();
                            if (player.currentScreenHandler.getCursorStack().isEmpty()) {
                                player.currentScreenHandler.setCursorStack(stack.copy());
                            } else if (ItemStack.areItemsAndComponentsEqual(stack, cursorStack) && cursorStack.getCount() < cursorStack.getMaxCount()) {
                                cursorStack.increment(1);
                            } else {
                                player.dropItem(stack.copy(), false);
                            }
                        })
                );
            });
        }
    }

    @Override
    public void onInput(String input) {
        super.onInput(input);
        apiDebounce = System.currentTimeMillis() + 500;
    }

    @Override
    public void onClose() {
        parentGui.open();
    }
}
