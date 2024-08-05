package io.github.sakurawald.module.initializer.command_toolbox.send_toast;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SendToastInitializer extends ModuleInitializer {
    private static final String IMPOSSIBLE = "impossible";

    void sendToast(ServerPlayerEntity player, AdvancementFrame advancementFrame, Item icon, Text title) {

        AdvancementDisplay advancementDisplay = new AdvancementDisplay(
                icon.getDefaultStack()
                , title
                , Text.empty()
                , Optional.of(Identifier.of("minecraft:textures/gui/advancements/backgrounds/end.png"))
                , advancementFrame
                , true
                , false
                , true
        );
        Identifier identifier = Identifier.of("custom", "custom");

        ImpossibleCriterion criterion = new ImpossibleCriterion();
        AdvancementCriterion<ImpossibleCriterion.Conditions> conditionsAdvancementCriterion = criterion.create(new ImpossibleCriterion.Conditions());

        AdvancementEntry advancementEntry = Advancement.Builder.create()
                .display(advancementDisplay)
                .rewards(AdvancementRewards.NONE)
                .requirements(AdvancementRequirements.anyOf(List.of(IMPOSSIBLE)))
                .criterion(IMPOSSIBLE, conditionsAdvancementCriterion)
                .build(identifier);

        AdvancementUpdateS2CPacket packet = makeAdvancementUpdateS2CPacket(advancementEntry, identifier);
        player.networkHandler.sendPacket(packet);
    }

    private static @NotNull AdvancementUpdateS2CPacket makeAdvancementUpdateS2CPacket(AdvancementEntry advancementEntry, Identifier identifier) {
        AdvancementProgress advancementProgress = new AdvancementProgress();
        AdvancementRequirements advancementRequirements = new AdvancementRequirements(List.of(List.of(IMPOSSIBLE)));
        advancementProgress.init(advancementRequirements);

        CriterionProgress criterionProgress = advancementProgress.getCriterionProgress(IMPOSSIBLE);
        criterionProgress.obtain();

        Collection<AdvancementEntry> toEarn = List.of(advancementEntry);
        Set<Identifier> toRemove = Set.of();
        Map<Identifier, AdvancementProgress> toSetProgress = Map.of(identifier, advancementProgress);
        return new AdvancementUpdateS2CPacket(false, toEarn, toRemove, toSetProgress);
    }

    @Command("send-toast")
    @CommandPermission(level = 4)
    int sendToast(@CommandSource CommandContext<ServerCommandSource> ctx
            , ServerPlayerEntity player
            , Optional<AdvancementFrame> toastType
            , Optional<Item> icon
            , GreedyString message
    ) {

        Item $icon = icon.orElse(Items.SLIME_BALL);
        AdvancementFrame $toastType = toastType.orElse(AdvancementFrame.CHALLENGE);
        Text title = MessageHelper.ofText(player, false, message.getString());
        this.sendToast(player, $toastType, $icon, title);

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

}
