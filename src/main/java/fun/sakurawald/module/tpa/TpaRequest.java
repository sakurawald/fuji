package fun.sakurawald.module.tpa;

import fun.sakurawald.config.ConfigManager;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.level.ServerPlayer;

import java.util.Timer;
import java.util.TimerTask;

import static fun.sakurawald.util.MessageUtil.resolve;

@ToString
public class TpaRequest {

    private static final String CIRCLE = "●";
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

    public Component asSenderComponent$Description() {
        return tpahere ? resolve(getSender(), "tpa.others_to_you", receiver.getGameProfile().getName())
                : resolve(getSender(), "tpa.you_to_others", receiver.getGameProfile().getName());
    }

    public Component asSenderComponent$Sent() {
        TextComponent cancelComponent = Component
                .text(CROSS).color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(resolve(getSender(), "cancel")))
                .clickEvent(ClickEvent.runCommand("/tpacancel %s".formatted(getReceiver().getGameProfile().getName())));

        return asSenderComponent$Description()
                .appendSpace()
                .append(cancelComponent);
    }

    public Component asReceiverComponent$Description() {
        return tpahere ? resolve(getReceiver(), "tpa.you_to_others", sender.getGameProfile().getName())
                : resolve(getReceiver(), "tpa.others_to_you", sender.getGameProfile().getName());
    }

    public Component asReceiverComponent$Sent() {
        Component acceptComponent = Component.text(TICK).color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(resolve(getReceiver(), "accept")))
                .clickEvent(ClickEvent.runCommand("/tpaaccept %s".formatted(sender.getGameProfile().getName())));
        Component denyComponent = Component.text(CROSS).color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(resolve(getReceiver(), "deny")))
                .clickEvent(ClickEvent.runCommand("/tpadeny %s".formatted(sender.getGameProfile().getName())));
        return asReceiverComponent$Description()
                .appendSpace()
                .append(acceptComponent)
                .appendSpace()
                .append(denyComponent);
    }

    public Component asSenderComponent$Accepted() {
        return asSenderComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.GREEN));
    }

    public Component asReceiverComponent$Accepted() {
        return asReceiverComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.GREEN));
    }

    public Component asSenderComponent$Denied() {
        return asSenderComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.RED));
    }

    public Component asReceiverComponent$Denied() {
        return asReceiverComponent$Description().appendSpace().append(Component.text(CIRCLE, NamedTextColor.RED));
    }

    public Component asSenderComponent$Cancelled() {
        return asSenderComponent$Description().decoration(TextDecoration.STRIKETHROUGH, true);
    }

    public Component asReceiverComponent$Cancelled() {
        return asReceiverComponent$Description().decoration(TextDecoration.STRIKETHROUGH, true);
    }
}
