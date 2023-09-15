package fun.sakurawald.module.tpa;

import fun.sakurawald.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class TpaRequest {

    final ServerPlayer teleportWho;
    final ServerPlayer teleportTo;
    final boolean tpahere;
    private Timer timer;

    public TpaRequest(ServerPlayer teleportWho, ServerPlayer teleportTo, boolean tpahere) {
        this.teleportWho = teleportWho;
        this.teleportTo = teleportTo;
        this.tpahere = tpahere;
    }


    public ServerPlayer getSender() {
        return tpahere ? teleportTo : teleportWho;
    }

    public ServerPlayer getReceiver() {
        return tpahere ? teleportWho : teleportTo;
    }

    public Component asSenderComponent$Sent() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }


    public Component asSenderComponent$Accept() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }

    public Component asReceiverComponent$Accept() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }

    public Component asSenderComponent$Deny() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }

    public Component asReceiverComponent$Deny() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }

    public Component asSenderComponent$Cancel() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }

    public Component asReceiverComponent$Cancel() {
        return Component.text("You've sent a request to %s".formatted(getReceiver().getGameProfile().getName()));
    }

    public Component asReceiverComponent$Sent() {
        String who = teleportWho.getName().getString();
        String to = teleportTo.getName().getString();
        Component description = tpahere ? Component.text("Teleport you to %s".formatted(who)) : Component.text("Teleport %s to you".formatted(who));
        Component ret = Component.empty()
                .append(description)
                .appendNewline()
                .append(Component.text("[Accept]").clickEvent(ClickEvent.runCommand("/tpaaccept %s".formatted(tpahere ? to : who))))
                .appendSpace()
                .append(Component.text("[Deny]").clickEvent(ClickEvent.runCommand("/tpadeny %s".formatted(tpahere ? to : who))));
        return ret;
    }

    void startTimeout(Runnable onTimeout) {
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        getSender().sendMessage(asReceiverComponent$Cancel());
                        getReceiver().sendMessage(asReceiverComponent$Cancel());
                        onTimeout.run();
                    }
                },
                ConfigManager.configWrapper.instance().modules.tpa.timeout * 1000L
        );
    }

    void cancelTimeout() {
        timer.cancel();
    }


}
