package io.github.sakurawald.module.initializer.world;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.annotation.Cite;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.Dimension;
import io.github.sakurawald.core.command.argument.wrapper.impl.DimensionType;
import io.github.sakurawald.core.command.exception.AbortOperationException;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.world.config.model.WorldConfigModel;
import io.github.sakurawald.module.initializer.world.config.model.WorldDataModel;
import io.github.sakurawald.module.initializer.world.structure.DimensionEntry;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSeed;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/*
 * RegistryKeys.DIMENSION_TYPE only returns registered `dimension type`.
 *
 * To list multi dimensions, use `RegistryKeys.DIMENSION`.
 * DimensionArgumentType.dimension() is equals to RegistryKeys.DIMENSION, but the DimensionArgumentType()'s suggestion will not suggest new added dimension types.
 *
 * The `RegistryKeys.WORLD` and `RegistryKeys.DIMENSION` can be cast to each other.
 * public static final RegistryKey<Registry<World>> WORLD = RegistryKeys.of("dimension");
 * public static final RegistryKey<Registry<DimensionOptions>> DIMENSION = RegistryKeys.of("dimension");
 */

@Cite("https://github.com/NucleoidMC/fantasy")
@Getter
@CommandRequirement(level = 4)
public class WorldInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<WorldConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, WorldConfigModel.class);

    private static final BaseConfigurationHandler<WorldDataModel> storage = new ObjectConfigurationHandler<>("world.json", WorldDataModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("world.json"), WorldInitializer.class));

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadWorlds);
    }

    public void loadWorlds(@NotNull MinecraftServer server) {
        storage.getModel().dimension_list.stream()
            .filter(DimensionEntry::isEnable)
            .forEach(it -> {
                try {
                    WorldManager.requestToCreateWorld(it);
                    LogUtil.info("load dimension {} into server successfully.", it.getDimension());
                } catch (Exception e) {
                    LogUtil.error("failed to load dimension `{}`", it, e);
                }
            });
    }

    private static void checkBlacklist(CommandContext<ServerCommandSource> ctx, String identifier) {
        if (config.getModel().blacklist.dimension_list.contains(identifier)) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "world.dimension.blacklist", identifier);
            throw new AbortOperationException();
        }
    }

    @CommandNode("world tp")
    private static int $tp(@CommandSource ServerPlayerEntity player, Dimension dimension) {
        ServerWorld world = dimension.getValue();
        BlockPos spawnPos = world.getSpawnPos();
        player.teleport(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("world list")
    private static int $list(@CommandSource ServerPlayerEntity player) {
        ServerHelper.getWorlds().forEach(world -> {
            String dimensionType = world.getDimensionEntry().getIdAsString();
            String dimension = String.valueOf(world.getRegistryKey().getValue());
            LocaleHelper.sendMessageByKey(player, "world.dimension.list.entry", dimension, dimensionType);
        });
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("world create")
    private static int $create(@CommandSource CommandContext<ServerCommandSource> ctx, String name,
                               Optional<Long> seed, DimensionType dimensionType) {

        /* make dimension identifier */
        String FUJI_DIMENSION_NAMESPACE = "fuji";
        Identifier dimensionIdentifier = Identifier.of(FUJI_DIMENSION_NAMESPACE, name);

        /* check exist */
        if (ServerHelper.getWorlds().stream().anyMatch(it -> RegistryHelper.ofString(it).equals(dimensionIdentifier.toString()))) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "world.dimension.exist");
            return CommandHelper.Return.FAIL;
        }

        /* make dimension entry */
        long $seed = seed.orElse(RandomSeed.getSeed());
        Identifier dimensionTypeIdentifier = Identifier.of(dimensionType.getValue());
        DimensionEntry dimensionEntry = new DimensionEntry(true, dimensionIdentifier.toString(), dimensionTypeIdentifier.toString(), $seed);
        storage.getModel().dimension_list.add(dimensionEntry);
        storage.writeStorage();

        /* request creation */
        WorldManager.requestToCreateWorld(dimensionEntry);

        LocaleHelper.sendBroadcastByKey("world.dimension.created", dimensionIdentifier);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("world delete")
    private static int $delete(@CommandSource CommandContext<ServerCommandSource> ctx, Dimension dimension) {
        /* check blacklist */
        ServerWorld world = dimension.getValue();
        String identifier = RegistryHelper.ofString(world);
        checkBlacklist(ctx, identifier);

        /* request to deletion */
        WorldManager.requestToDeleteWorld(world);

        /* write entry */
        Optional<DimensionEntry> first = storage.getModel().dimension_list.stream().filter(o -> o.getDimension().equals(identifier)).findFirst();
        if (first.isEmpty()) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "world.dimension.not_found", identifier);
            return CommandHelper.Return.FAIL;
        }
        storage.getModel().dimension_list.remove(first.get());
        storage.writeStorage();

        LocaleHelper.sendBroadcastByKey("world.dimension.deleted", identifier);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("world reset")
    private static int $reset(@CommandSource CommandContext<ServerCommandSource> ctx, Optional<Boolean> useTheSameSeed, Dimension dimension) {
        // draw seed and save
        ServerWorld world = dimension.getValue();
        String identifier = RegistryHelper.ofString(world);
        checkBlacklist(ctx, identifier);

        Optional<DimensionEntry> dimensionEntryOpt = storage.getModel().dimension_list.stream().filter(o -> o.getDimension().equals(identifier)).findFirst();
        if (dimensionEntryOpt.isEmpty()) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "world.dimension.not_found");
            return CommandHelper.Return.FAIL;
        }

        // request the deletion
        WorldManager.requestToDeleteWorld(world);

        // set the new seed
        Boolean $useTheSameSeed = useTheSameSeed.orElse(false);
        long newSeed = $useTheSameSeed ? dimensionEntryOpt.get().getSeed() : RandomSeed.getSeed();
        dimensionEntryOpt.get().setSeed(newSeed);
        storage.writeStorage();

        // request the creation
        WorldManager.requestToCreateWorld(dimensionEntryOpt.get());

        LocaleHelper.sendBroadcastByKey("world.dimension.reset", identifier);
        return CommandHelper.Return.SUCCESS;
    }
}
