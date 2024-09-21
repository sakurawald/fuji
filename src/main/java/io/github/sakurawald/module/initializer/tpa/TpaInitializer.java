package io.github.sakurawald.module.initializer.tpa;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.job.impl.MentionPlayersJob;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tpa.config.model.TpaConfigModel;
import io.github.sakurawald.module.initializer.tpa.structure.TpaRequest;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TpaInitializer extends ModuleInitializer {

    public final BaseConfigurationHandler<TpaConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, TpaConfigModel.class);

    @Getter
    private final List<TpaRequest> requests = new ArrayList<>();

    @CommandNode("tpa")
    private int $tpa(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doRequest(player, target, false);
    }

    @CommandNode("tpahere")
    private int $tpahere(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doRequest(player, target, true);
    }

    @CommandNode("tpaaccept")
    private int $tpaaccept(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doResponse(player, target, ResponseStatus.ACCEPT);
    }

    @CommandNode("tpadeny")
    private int $tpadeny(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doResponse(player, target, ResponseStatus.DENY);
    }

    @CommandNode("tpacancel")
    private int $tpacancel(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doResponse(player, target, ResponseStatus.CANCEL);
    }

    private int doResponse(ServerPlayerEntity source, ServerPlayerEntity target, ResponseStatus status) {
        /* resolve relative request */
        Optional<TpaRequest> requestOptional = requests.stream()
                .filter(request ->
                        status == ResponseStatus.CANCEL ?
                                (request.getSender().equals(source) && request.getReceiver().equals(target))
                                : (request.getSender().equals(target) && request.getReceiver().equals(source)))
                .findFirst();
        if (requestOptional.isEmpty()) {
            LocaleHelper.sendActionBarByKey(source, "tpa.no_relative_ticket");
            return CommandHelper.Return.FAIL;
        }

        TpaRequest request = requestOptional.get();
        if (status == ResponseStatus.ACCEPT) {
            request.getSender().sendActionBar(request.asSenderComponent$Accepted());
            request.getReceiver().sendMessage(request.asReceiverComponent$Accepted());

            ServerPlayerEntity who = request.getTeleportWho();
            ServerPlayerEntity to = request.getTeleportTo();
            MentionPlayersJob.requestJob(config.getModel().mention_player, request.isTpahere() ? to : who);
            who.teleport((ServerWorld) to.getWorld(), to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
        } else if (status == ResponseStatus.DENY) {
            request.getSender().sendActionBar(request.asSenderComponent$Denied());
            request.getReceiver().sendMessage(request.asReceiverComponent$Denied());
        } else if (status == ResponseStatus.CANCEL) {
            request.getSender().sendMessage(request.asSenderComponent$Cancelled());
            request.getReceiver().sendMessage(request.asReceiverComponent$Cancelled());
        }

        request.cancelTimeout();
        requests.remove(request);
        return CommandHelper.Return.SUCCESS;
    }

    private int doRequest(ServerPlayerEntity source, ServerPlayerEntity target, boolean tpahere) {
        /* add request */
        TpaRequest request = new TpaRequest(source, target, tpahere);

        /* has similar request ? */
        if (request.getSender().equals(request.getReceiver())) {
            LocaleHelper.sendActionBarByKey(request.getSender(), "tpa.request_to_self");

            return CommandHelper.Return.FAIL;
        }

        if (requests.stream().anyMatch(request::similarTo)) {
            LocaleHelper.sendActionBarByKey(request.getSender(), "tpa.similar_request_exists");
            return CommandHelper.Return.FAIL;
        }

        requests.add(request);
        request.startTimeout();

        /* feedback */
        request.getReceiver().sendMessage(request.asReceiverComponent$Sent());
        MentionPlayersJob.requestJob(config.getModel().mention_player, request.getReceiver());
        request.getSender().sendMessage(request.asSenderComponent$Sent());
        return CommandHelper.Return.SUCCESS;
    }

    private enum ResponseStatus {ACCEPT, DENY, CANCEL}
}
