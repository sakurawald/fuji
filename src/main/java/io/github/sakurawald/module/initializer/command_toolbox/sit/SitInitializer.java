package io.github.sakurawald.module.initializer.command_toolbox.sit;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.*;

public class SitInitializer extends ModuleInitializer {

    public final Set<Entity> CHAIRS = new HashSet<>();

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("sit").executes(this::$sit));
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            CHAIRS.forEach(e -> {
                if (e.isAlive()) e.kill();
            });
        });
    }

    private int $sit(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, (player) -> {

            BlockState blockState = player.getEntityWorld().getBlockState(new BlockPos(player.getBlockX(), player.getBlockY() - 1, player.getBlockZ()));
            if (player.hasVehicle() || player.isFallFlying() || player.isSleeping() || player.isSwimming() || player.isSpectator() || blockState.isAir() || blockState.isLiquid())
                return 0;

            Entity entity = createChair(player.getEntityWorld(), player.getBlockPos(), new Vec3d(0, -1.7, 0), player.getPos(), false);
            CHAIRS.add(entity);
            player.startRiding(entity, true);

            return CommandHelper.Return.SUCCESS;
        });
    }

    public Entity createChair(World world, BlockPos blockPos, Vec3d blockPosOffset, @Nullable Vec3d target, boolean boundToBlock) {
        ArmorStandEntity entity = new ArmorStandEntity(world, 0.5d + blockPos.getX() + blockPosOffset.getX(), blockPos.getY() + blockPosOffset.getY(), 0.5d + blockPos.getZ() + blockPosOffset.getZ()) {
            private boolean v = false;

            @Override
            protected void addPassenger(Entity passenger) {
                super.addPassenger(passenger);
                v = true;
            }

            @Override
            public boolean canMoveVoluntarily() {
                return false;
            }

            @Override
            public boolean collidesWithStateAtPos(BlockPos blockPos, BlockState blockState) {
                return false;
            }

            @Override
            public void tick() {
                if (v && getPassengerList().isEmpty()) {
                    kill();
                }

                if (getEntityWorld().getBlockState(getBlockPos()).isAir() && boundToBlock) {
                    kill();
                }
                super.tick();
            }

        };

        if (target != null)
            entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.subtract(0, (target.getY() * 2), 0));
        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setCustomName(Text.literal("FUJI-SIT"));
        entity.setNoGravity(true);
        world.spawnEntity(entity);
        return entity;
    }
}
