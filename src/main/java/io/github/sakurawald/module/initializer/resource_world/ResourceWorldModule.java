package io.github.sakurawald.module.initializer.resource_world;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Lifecycle;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.newbie_welcome.random_teleport.RandomTeleport;
import io.github.sakurawald.module.initializer.resource_world.interfaces.DimensionOptionsMixinInterface;
import io.github.sakurawald.module.initializer.resource_world.interfaces.SimpleRegistryMixinInterface;
import io.github.sakurawald.module.mixin.resource_world.MinecraftServerAccessor;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.RandomSupport;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import static net.minecraft.commands.Commands.literal;


public class ResourceWorldModule extends ModuleInitializer {

    private final String DEFAULT_RESOURCE_WORLD_NAMESPACE = "resource_world";
    private final String DEFAULT_THE_NETHER_PATH = "the_nether";
    private final String DEFAULT_THE_END_PATH = "the_end";
    private final String DEFAULT_OVERWORLD_PATH = "overworld";

    @Override
    public void onInitialize() {
        ServerWorldEvents.UNLOAD.register(this::onWorldUnload);
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            this.loadWorlds(server);
            this.registerScheduleTask(server);
        });
    }

    public void registerScheduleTask(MinecraftServer server) {
        ScheduleUtil.addJob(ResourceWorldAutoResetJob.class, null, null, Configs.configHandler.model().modules.resource_world.auto_reset_cron, new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
                this.put(ResourceWorldModule.class.getName(), ResourceWorldModule.this);
            }
        });
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("rw")
                        .then(literal("reset").requires(source -> source.hasPermission(4)).executes(this::$reset))
                        .then(literal("delete").requires(source -> source.hasPermission(4)).then(literal(DEFAULT_OVERWORLD_PATH).executes(this::$delete))
                                .then(literal(DEFAULT_THE_NETHER_PATH).executes(this::$delete))
                                .then(literal(DEFAULT_THE_END_PATH).executes(this::$delete)))
                        .then(literal("tp").then(literal(DEFAULT_OVERWORLD_PATH).executes(this::$tp))
                                .then(literal(DEFAULT_THE_NETHER_PATH).executes(this::$tp))
                                .then(literal(DEFAULT_THE_END_PATH).executes(this::$tp))
                        )
        );
    }

    private int $reset(CommandContext<CommandSourceStack> ctx) {
        resetWorlds(ctx.getSource().getServer());
        return Command.SINGLE_SUCCESS;
    }

    private void resetWorlds(MinecraftServer server) {
        MessageUtil.sendBroadcast("resource_world.world.reset");
        Configs.configHandler.model().modules.resource_world.seed = RandomSupport.generateUniqueSeed();
        Configs.configHandler.saveToDisk();
        deleteWorld(server, DEFAULT_OVERWORLD_PATH);
        deleteWorld(server, DEFAULT_THE_NETHER_PATH);
        deleteWorld(server, DEFAULT_THE_END_PATH);
    }

    public void loadWorlds(MinecraftServer server) {
        long seed = Configs.configHandler.model().modules.resource_world.seed;

        ConfigModel.Modules.ResourceWorld.ResourceWorlds resourceWorlds = Configs.configHandler.model().modules.resource_world.resource_worlds;
        if (resourceWorlds.enable_overworld) {
            createWorld(server, BuiltinDimensionTypes.OVERWORLD, DEFAULT_OVERWORLD_PATH, seed);
        }
        if (resourceWorlds.enable_the_nether) {
            createWorld(server, BuiltinDimensionTypes.NETHER, DEFAULT_THE_NETHER_PATH, seed);
        }
        if (resourceWorlds.enable_the_end) {
            createWorld(server, BuiltinDimensionTypes.END, DEFAULT_THE_END_PATH, seed);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private ChunkGenerator getChunkGenerator(MinecraftServer server, ResourceKey<DimensionType> dimensionTypeRegistryKey) {
        if (dimensionTypeRegistryKey == BuiltinDimensionTypes.OVERWORLD) {
            return server.getLevel(Level.OVERWORLD).getChunkSource().getGenerator();
        }
        if (dimensionTypeRegistryKey == BuiltinDimensionTypes.NETHER) {
            return server.getLevel(Level.NETHER).getChunkSource().getGenerator();
        }
        if (dimensionTypeRegistryKey == BuiltinDimensionTypes.END) {
            return server.getLevel(Level.END).getChunkSource().getGenerator();
        }
        return null;
    }

    private LevelStem createDimensionOptions(MinecraftServer server, ResourceKey<DimensionType> dimensionTypeRegistryKey) {
        Holder<DimensionType> dimensionTypeRegistryEntry = getDimensionTypeRegistryEntry(server, dimensionTypeRegistryKey);
        ChunkGenerator chunkGenerator = getChunkGenerator(server, dimensionTypeRegistryKey);
        //noinspection DataFlowIssue
        return new LevelStem(dimensionTypeRegistryEntry, chunkGenerator);
    }

    private Holder<DimensionType> getDimensionTypeRegistryEntry(MinecraftServer server, ResourceKey<DimensionType> dimensionTypeRegistryKey) {
        return server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolder(dimensionTypeRegistryKey).orElse(null);
    }

    private ResourceKey<DimensionType> getDimensionTypeRegistryKeyByPath(String path) {
        if (path.equals(DEFAULT_OVERWORLD_PATH)) return BuiltinDimensionTypes.OVERWORLD;
        if (path.equals(DEFAULT_THE_NETHER_PATH)) return BuiltinDimensionTypes.NETHER;
        if (path.equals(DEFAULT_THE_END_PATH)) return BuiltinDimensionTypes.END;
        return null;
    }


    private MappedRegistry<LevelStem> getDimensionOptionsRegistry(MinecraftServer server) {
        RegistryAccess registryManager = server.registries().compositeAccess();
        return (MappedRegistry<LevelStem>) registryManager.registryOrThrow(Registries.LEVEL_STEM);
    }

    @SuppressWarnings("deprecation")
    private void createWorld(MinecraftServer server, ResourceKey<DimensionType> dimensionTypeRegistryKey, String path, long seed) {
        /* create the world */
        // note: we use the same WorldData from OVERWORLD
        ResourceWorldProperties resourceWorldProperties = new ResourceWorldProperties(server.getWorldData(), seed);
        ResourceKey<Level> worldRegistryKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(DEFAULT_RESOURCE_WORLD_NAMESPACE, path));
        LevelStem dimensionOptions = createDimensionOptions(server, dimensionTypeRegistryKey);
        MinecraftServerAccessor serverAccessor = (MinecraftServerAccessor) server;
        ServerLevel world = new ResourceWorld(server,
                Util.backgroundExecutor(),
                serverAccessor.getStorageSource(),
                resourceWorldProperties,
                worldRegistryKey,
                dimensionOptions,
                VoidWorldGenerationProgressListener.INSTANCE,
                false,
                BiomeManager.obfuscateSeed(seed),
                ImmutableList.of(),
                true,
                null);

        if (dimensionTypeRegistryKey == BuiltinDimensionTypes.END) {
            world.setDragonFight(new EndDragonFight(world, world.getSeed(), EndDragonFight.Data.DEFAULT));
        }

        /* register the world */
        ((DimensionOptionsMixinInterface) (Object) dimensionOptions).fuji$setSaveProperties(false);

        MappedRegistry<LevelStem> dimensionsRegistry = getDimensionOptionsRegistry(server);
        boolean isFrozen = ((SimpleRegistryMixinInterface<?>) dimensionsRegistry).fuji$isFrozen();
        ((SimpleRegistryMixinInterface<?>) dimensionsRegistry).fuji$setFrozen(false);
        var dimensionOptionsRegistryKey = ResourceKey.create(Registries.LEVEL_STEM, worldRegistryKey.location());
        if (!dimensionsRegistry.containsKey(dimensionOptionsRegistryKey)) {
            dimensionsRegistry.register(dimensionOptionsRegistryKey, dimensionOptions, Lifecycle.stable());
        }
        ((SimpleRegistryMixinInterface<?>) dimensionsRegistry).fuji$setFrozen(isFrozen);

        serverAccessor.getLevels().put(world.dimension(), world);
        ServerWorldEvents.LOAD.invoker().onWorldLoad(server, world);
        world.tick(() -> true);
        MessageUtil.sendBroadcast("resource_world.world.created", path);
    }

    private ServerLevel getResourceWorldByPath(MinecraftServer server, String path) {
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(DEFAULT_RESOURCE_WORLD_NAMESPACE, path));
        return server.getLevel(worldKey);
    }

    private int $tp(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            String path = ctx.getNodes().get(2).getNode().getName();
            ServerLevel world = getResourceWorldByPath(ctx.getSource().getServer(), path);
            if (world == null) {
                MessageUtil.sendMessage(ctx.getSource(), "resource_world.world.not_found", path);
                return 0;
            }

            if (world.dimensionTypeId() == BuiltinDimensionTypes.END) {
                ServerLevel.makeObsidianPlatform(world);
                BlockPos endSpawnPos = ServerLevel.END_SPAWN_POINT;
                player.teleportTo(world, endSpawnPos.getX() + 0.5, endSpawnPos.getY(), endSpawnPos.getZ() + 0.5, 90, 0);
            } else {
                MessageUtil.sendActionBar(player, "resource_world.world.tp.tip");
                RandomTeleport.randomTeleport(player, world, false);
            }

            return Command.SINGLE_SUCCESS;
        });
    }


    private void deleteWorld(MinecraftServer server, String path) {
        ServerLevel world = getResourceWorldByPath(server, path);
        if (world == null) return;

        ResourceWorldManager.enqueueWorldDeletion(world);
        MessageUtil.sendBroadcast("resource_world.world.deleted", path);
    }

    private int $delete(CommandContext<CommandSourceStack> ctx) {
        String path = ctx.getNodes().get(2).getNode().getName();
        deleteWorld(ctx.getSource().getServer(), path);
        return Command.SINGLE_SUCCESS;
    }

    public void onWorldUnload(MinecraftServer server, ServerLevel world) {
        if (server.isRunning()) {
            String namespace = world.dimension().location().getNamespace();
            String path = world.dimension().location().getPath();
            // IMPORTANT: only delete the world if it's a resource world
            if (!namespace.equals(DEFAULT_RESOURCE_WORLD_NAMESPACE)) return;

            Fuji.LOGGER.info("onWorldUnload() -> Creating world {} ...", path);
            long seed = Configs.configHandler.model().modules.resource_world.seed;
            this.createWorld(server, this.getDimensionTypeRegistryKeyByPath(path), path, seed);
        }
    }

    public static class ResourceWorldAutoResetJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            Fuji.LOGGER.info("Start to reset resource worlds.");
            MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
            ResourceWorldModule module = (ResourceWorldModule) context.getJobDetail().getJobDataMap().get(ResourceWorldModule.class.getName());
            server.execute(() -> module.resetWorlds(server));
        }
    }
}
