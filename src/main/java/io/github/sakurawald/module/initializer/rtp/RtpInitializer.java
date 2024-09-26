package io.github.sakurawald.module.initializer.rtp;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.Dimension;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.service.random_teleport.RandomTeleporter;
import io.github.sakurawald.core.structure.TeleportSetup;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.rtp.config.model.RtpConfigModel;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class RtpInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<RtpConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, RtpConfigModel.class);

    private static @NotNull Optional<TeleportSetup> getTeleportSetup(@NotNull ServerWorld world) {
        List<TeleportSetup> list = config.getModel().setup.dimension;
        String dimension = RegistryHelper.ofString(world);
        return list.stream().filter(o -> o.getDimension().equals(dimension)).findFirst();
    }

    @CommandNode("rtp")
    private static int $rtp(@CommandSource ServerPlayerEntity player, Optional<Dimension> dimension) {

        ServerWorld serverWorld = dimension.isPresent() ? dimension.get().getValue() : player.getServerWorld();

        Optional<TeleportSetup> first = getTeleportSetup(serverWorld);
        if (first.isEmpty()) {
            LocaleHelper.sendMessageByKey(player, "rtp.dimension.disallow", RegistryHelper.ofString(serverWorld));
            return CommandHelper.Return.FAIL;
        }

        LocaleHelper.sendActionBarByKey(player, "rtp.tip");
        RandomTeleporter.request(player, first.get(), (position -> LocaleHelper.sendMessageByKey(player, "rtp.success")));
        return CommandHelper.Return.SUCCESS;
    }
}
