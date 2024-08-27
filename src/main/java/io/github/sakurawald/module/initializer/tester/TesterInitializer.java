package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


@CommandNode("tester")
@CommandRequirement(level = 4)
public class TesterInitializer extends ModuleInitializer {

    @CommandNode("run")
    private static int $run(@CommandSource CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();

//        player.setNoGravity(false);
//        player.setNoGravity(false);

//        PlayerAbilities playerAbilities = new PlayerAbilities();
//        playerAbilities.allowFlying = true;
//        playerAbilities.flying = true;
//        player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(playerAbilities));

//        public final double getFinalGravity() {
//        player.setNoGravity(!player.hasNoGravity());
        // no damage
//        player.getFinalGravity()

//        player.setNoGravity(false);

//        player.setInvisible(true);
//        player.isInvisibleTo()

//        player.isCollidable()
//        player.isAttackable()
//        player.isInCreativeMode()
//        player.isPushable()
//        player.isSpectator()
//        player.setInvisible(true);
//        player.isCollidable();
//        player.isTarget()
//        player.isInRange()
//        player.canHaveStatusEffect()
//        player.canTarget()

//        player.isInvisibleTo()

        return -1;
    }

}
