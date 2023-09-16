package fun.sakurawald.module.tpa;

import fun.sakurawald.config.ConfigManager;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.level.ServerPlayer;

import java.util.Timer;
import java.util.TimerTask;

@ToString
public class TpaRequest {

    private static final String TICK = "[✔]";
    private static final String CROSS = "[❌]";
    @Getter
    private final ServerPlayer sender;
    @Getter
    private final ServerPlayer receiver;
    private final boolean tpahere;
    private Timer timer;

    public TpaRequest(ServerPlayer sender, ServerPlayer receiver, boolean tpahere) {
        this.sender = sender;
        this.receiver = receiver;
        this.tpahere = tpahere;
    }

    boolean similarTo(TpaRequest other) {
        return (this.sender.equals(other.sender) && this.receiver.equals(other.receiver)) ||
                (this.sender.equals(other.receiver) && this.receiver.equals(other.sender));
    }

    public ServerPlayer getTeleportWho() {
        return tpahere ? getReceiver() : getSender();
    }

    public ServerPlayer getTeleportTo() {
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
                        TpaModule.getRequests().remove(that);
                    }
                },
                ConfigManager.configWrapper.instance().modules.tpa.timeout * 1000L
        );
    }

    public void cancelTimeout() {
        timer.cancel();
    }


    public Component asSenderComponent$Sent() {
        TextComponent cancelComponent = Component
                .text(CROSS).color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("Cancel", NamedTextColor.RED)))
                .clickEvent(ClickEvent.runCommand("/tpacancel %s".formatted(getReceiver().getGameProfile().getName())));

        return Component.text("Sent a request to %s".formatted(getReceiver().getGameProfile().getName())).color(NamedTextColor.YELLOW)
                .appendSpace()
                .append(cancelComponent);
    }

    public Component asReceiverComponent$Sent() {
        String sender = this.sender.getName().getString();
        Component description = (tpahere ? Component
                .text("You --> %s".formatted(sender)) : Component.text("%s --> You".formatted(sender)))
                .color(NamedTextColor.YELLOW);
        Component ret = Component.empty()
                .append(description
                        .appendSpace()
                        .append(Component.text(TICK).color(NamedTextColor.GREEN)
                                .hoverEvent(HoverEvent.showText(Component.text("Accept this request", NamedTextColor.GREEN)))
                                .clickEvent(ClickEvent.runCommand("/tpaaccept %s".formatted(sender))))
                        .appendSpace()
                        .append(Component.text(CROSS).color(NamedTextColor.RED)
                                .hoverEvent(HoverEvent.showText(Component.text("Deny this request", NamedTextColor.RED))))
                        .clickEvent(ClickEvent.runCommand("/tpadeny %s".formatted(sender))));
        return ret;
    }

    public Component asSenderComponent$Accepted() {
        return Component.text("%s has accepted your request".formatted(getReceiver().getGameProfile().getName()), NamedTextColor.YELLOW);
    }

    public Component asReceiverComponent$Accepted() {
        return Component.text("Accepted the request from %s".formatted(getSender().getGameProfile().getName()), NamedTextColor.YELLOW);
    }

    public Component asSenderComponent$Denied() {
        return Component.text("%s has denied your request".formatted(getReceiver().getGameProfile().getName()), NamedTextColor.YELLOW);
    }

    public Component asReceiverComponent$Denied() {
        return Component.text("Denied the request from %s".formatted(getSender().getGameProfile().getName()), NamedTextColor.YELLOW);
    }

    public Component asSenderComponent$Cancelled() {
        return Component.text("Cancelled the request to %s".formatted(getReceiver().getGameProfile().getName()), NamedTextColor.YELLOW);
    }

    public Component asReceiverComponent$Cancelled() {
        return Component.text("Request from %s has been canceled".formatted(getSender().getGameProfile().getName()), NamedTextColor.YELLOW);
    }
}
