package io.github.sakurawald.module.initializer.tpa.structure;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.tpa.TpaInitializer;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

@ToString
public class TpaRequest {

    private static final String CIRCLE = "●";
    private static final String TICK = "[✔]";
    private static final String CROSS = "[❌]";

    private static final TpaInitializer module = Managers.getModuleManager().getInitializer(TpaInitializer.class);

    @Getter
    private final ServerPlayerEntity sender;
    @Getter
    private final ServerPlayerEntity receiver;
    @Getter
    private final boolean tpahere;
    private Timer timer;

    public TpaRequest(ServerPlayerEntity sender, ServerPlayerEntity receiver, boolean tpahere) {
        this.sender = sender;
        this.receiver = receiver;
        this.tpahere = tpahere;
    }

    public boolean similarTo(@NotNull TpaRequest other) {
        return (this.sender.equals(other.sender) && this.receiver.equals(other.receiver)) ||
                (this.sender.equals(other.receiver) && this.receiver.equals(other.sender));
    }

    public ServerPlayerEntity getTeleportWho() {
        return tpahere ? getReceiver() : getSender();
    }

    public ServerPlayerEntity getTeleportTo() {
        return tpahere ? getSender() : getReceiver();
    }

    public void startTimeout() {
        var that = this;
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        getSender().sendMessage(asSenderComponent$Cancelled());
                        getReceiver().sendMessage(asReceiverComponent$Cancelled());
                        // don't forget to remove this request
                        module.getRequests().remove(that);
                    }
                },
                Configs.configHandler.model().modules.tpa.timeout * 1000L
        );
    }

    public void cancelTimeout() {
        timer.cancel();
    }

    public @NotNull Component asSenderComponent$Description() {
        return tpahere ? MessageHelper.ofComponent(getSender(), "tpa.others_to_you", receiver.getGameProfile().getName())
                : MessageHelper.ofComponent(getSender(), "tpa.you_to_others", receiver.getGameProfile().getName());
    }

    public @NotNull Component asSenderComponent$Sent() {
        TextComponent cancelComponent = Component
                .text(CROSS).color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(MessageHelper.ofComponent(getSender(), "cancel")))
                .clickEvent(ClickEvent.runCommand("/tpacancel %s".formatted(getReceiver().getGameProfile().getName())));

        return asSenderComponent$Description()
                .appendSpace()
                .append(cancelComponent);
    }

    public @NotNull Component asReceiverComponent$Description() {
        return tpahere ? MessageHelper.ofComponent(getReceiver(), "tpa.you_to_others", sender.getGameProfile().getName())
                : MessageHelper.ofComponent(getReceiver(), "tpa.others_to_you", sender.getGameProfile().getName());
    }

    public @NotNull Component asReceiverComponent$Sent() {
        Component acceptComponent = Component.text(TICK).color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(MessageHelper.ofComponent(getReceiver(), "accept")))
                .clickEvent(ClickEvent.runCommand("/tpaaccept %s".formatted(sender.getGameProfile().getName())));
        Component denyComponent = Component.text(CROSS).color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(MessageHelper.ofComponent(getReceiver(), "deny")))
                .clickEvent(ClickEvent.runCommand("/tpadeny %s".formatted(sender.getGameProfile().getName())));
        return asReceiverComponent$Description()
                .appendSpace()
                .append(acceptComponent)
                .appendSpace()
                .append(denyComponent);
    }

    public @NotNull Component asSenderComponent$Accepted() {
        return asSenderComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.GREEN));
    }

    public @NotNull Component asReceiverComponent$Accepted() {
        return asReceiverComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.GREEN));
    }

    public @NotNull Component asSenderComponent$Denied() {
        return asSenderComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.RED));
    }

    public @NotNull Component asReceiverComponent$Denied() {
        return asReceiverComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.RED));
    }

    public @NotNull Component asSenderComponent$Cancelled() {
        return asSenderComponent$Description().decoration(TextDecoration.STRIKETHROUGH, true);
    }

    public @NotNull Component asReceiverComponent$Cancelled() {
        return asReceiverComponent$Description().decoration(TextDecoration.STRIKETHROUGH, true);
    }
}
