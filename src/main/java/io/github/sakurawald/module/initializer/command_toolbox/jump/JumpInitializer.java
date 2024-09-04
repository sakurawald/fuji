package io.github.sakurawald.module.initializer.command_toolbox.jump;

import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class JumpInitializer extends ModuleInitializer {

    @CommandNode("jump")
    int jump(@CommandSource ServerPlayerEntity player, Optional<Integer> distance) {
        int $distance = distance.orElse(128);
        HitResult raycast = player.raycast($distance,0,false);
        Vec3d hitPos = raycast.getPos();
        player.teleport(player.getServerWorld(),hitPos.x, hitPos.y, hitPos.z,player.getYaw(), player.getPitch());
        return CommandHelper.Return.SUCCESS;
    }
}
