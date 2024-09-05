package io.github.sakurawald.module.initializer.echo.send_toast;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SendToastInitializer extends ModuleInitializer {
    private static final String IMPOSSIBLE = "impossible";

    private void sendToast(ServerPlayerEntity player, AdvancementFrame advancementFrame, Item icon, Text title) {
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

        player.networkHandler.sendPacket(makeGrantPacket(advancementEntry, identifier));
        player.networkHandler.sendPacket(makeRevokePacket(identifier));
    }

    private @NotNull AdvancementUpdateS2CPacket makeGrantPacket(AdvancementEntry advancementEntry, Identifier identifier) {
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

    private @NotNull AdvancementUpdateS2CPacket makeRevokePacket(Identifier identifier) {
        Collection<AdvancementEntry> toEarn = List.of();
        Set<Identifier> toRemove = Set.of(identifier);
        Map<Identifier, AdvancementProgress> toSetProgress = Map.of();
        return new AdvancementUpdateS2CPacket(false, toEarn, toRemove, toSetProgress);
    }

    @CommandNode("send-toast")
    @CommandRequirement(level = 4)
    int sendToast(@CommandSource CommandContext<ServerCommandSource> ctx
            , ServerPlayerEntity player
            , Optional<AdvancementFrame> toastType
            , Optional<Item> icon
            , GreedyString message
    ) {

        Item $icon = icon.orElse(Items.SLIME_BALL);
        AdvancementFrame $toastType = toastType.orElse(AdvancementFrame.CHALLENGE);
        Text title = MessageHelper.ofText(player, false, message.getValue());
        this.sendToast(player, $toastType, $icon, title);

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

}
