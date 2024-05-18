package io.github.sakurawald.module.initializer.head.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.head.HeadModule;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class PlayerInputGui extends AnvilInputGui {
    final HeadModule module = ModuleManager.getInitializer(HeadModule.class);
    private final HeadGui parentGui;
    private final ItemStack outputStack = Items.PLAYER_HEAD.getDefaultStack();
    private long apiDebounce = 0;

    public PlayerInputGui(HeadGui parentGui) {
        super(parentGui.player, false);
        this.parentGui = parentGui;
        this.setDefaultInputValue("");
        this.setSlot(1, Items.PLAYER_HEAD.getDefaultStack());
        this.setSlot(2, outputStack);
        this.setTitle(MessageUtil.ofVomponent(player, "head.category.player"));
    }

    @Override
    public void onTick() {
        if (apiDebounce != 0 && apiDebounce <= System.currentTimeMillis()) {
            apiDebounce = 0;

            CompletableFuture.runAsync(() -> {
                MinecraftServer server = player.server;
                UserCache profileCache = server.getUserCache();
                if (profileCache == null) {
                    outputStack.removeSubNbt("SkullOwner");
                    return;
                }

                Optional<GameProfile> possibleProfile = profileCache.findByName(this.getInput());
                MinecraftSessionService sessionService = server.getSessionService();
                if (possibleProfile.isEmpty()) {
                    outputStack.removeSubNbt("SkullOwner");
                    return;
                }

                ProfileResult profileResult = sessionService.fetchProfile(possibleProfile.get().getId(), false);
                if (profileResult == null) {
                    outputStack.removeSubNbt("SkullOwner");
                    return;
                }

                GameProfile profile = profileResult.profile();

                MinecraftProfileTextures textures = sessionService.getTextures(profile);
                if (textures == MinecraftProfileTextures.EMPTY) {
                    outputStack.removeSubNbt("SkullOwner");
                    return;
                }

                MinecraftProfileTexture texture = textures.skin();
                NbtCompound ownerTag = outputStack.getOrCreateSubNbt("SkullOwner");
                ownerTag.putUuid("Id", profile.getId());
                ownerTag.putString("Name", profile.getName());

                NbtCompound propertiesTag = new NbtCompound();
                NbtList texturesTag = new NbtList();
                NbtCompound textureValue = new NbtCompound();

                textureValue.putString("Value", new String(Base64.getEncoder().encode(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", texture.getUrl()).getBytes()), StandardCharsets.UTF_8));

                texturesTag.add(textureValue);
                propertiesTag.put("textures", texturesTag);
                ownerTag.put("Properties", propertiesTag);

                var builder = GuiElementBuilder.from(outputStack);
                if (Configs.headHandler.model().economyType != HeadModule.EconomyType.FREE) {
                    builder.addLoreLine(Text.empty());
                    builder.addLoreLine(MessageUtil.ofVomponent(player, "head.price").copy().append(module.getCost()));
                }

                this.setSlot(2, builder.asStack(), (index, type, action, gui) ->
                        module.tryPurchase(player, 1, () -> {
                            var cursorStack = getPlayer().currentScreenHandler.getCursorStack();
                            if (player.currentScreenHandler.getCursorStack().isEmpty()) {
                                player.currentScreenHandler.setCursorStack(outputStack.copy());
                            } else if (ItemStack.canCombine(outputStack, cursorStack) && cursorStack.getCount() < cursorStack.getMaxCount()) {
                                cursorStack.increment(1);
                            } else {
                                player.dropItem(outputStack.copy(), false);
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
