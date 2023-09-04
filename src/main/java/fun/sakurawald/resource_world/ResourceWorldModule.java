package fun.sakurawald.resource_world;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.ModMain;
import fun.sakurawald.mixin.MinecraftServerAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.literal;

public class ResourceWorldModule {

    public static final String DEFAULT_WORLD_PREFIX = "resource_world";
    public static final String DEFAULT_THE_NETHER_PATH = "the_nether";
    public static final String DEFAULT_THE_END_PATH = "the_end";
    private static final String DEFAULT_OVERWORLD_PATH = "overworld";
    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    public static void registerScheduleTask(MinecraftServer server) {
        LocalTime now = LocalTime.now();
        long initialDelay = LocalTime.of(20, 0).toSecondOfDay() - now.toSecondOfDay();
        if (initialDelay < 0) {
            initialDelay += TimeUnit.DAYS.toSeconds(1);
        }
        executorService.scheduleAtFixedRate(() -> {
            ModMain.LOGGER.info("Start to reset resource worlds.");
            server.execute(() -> ResourceWorldModule.resetWorlds(server));
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public static LiteralCommandNode<ServerCommandSource> registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        return dispatcher.register(
                CommandManager.literal("rw")
                        .then(literal("reset").requires(source -> source.hasPermissionLevel(4)).executes(ResourceWorldModule::resetWorlds))
                        .then(literal("create").requires(source -> source.hasPermissionLevel(4)).then(literal(DEFAULT_OVERWORLD_PATH).executes(ResourceWorldModule::createWorld))
                                .then(literal(DEFAULT_THE_NETHER_PATH).executes(ResourceWorldModule::createWorld))
                                .then(literal(DEFAULT_THE_END_PATH).executes(ResourceWorldModule::createWorld)))
                        .then(literal("delete").requires(source -> source.hasPermissionLevel(4)).then(literal(DEFAULT_OVERWORLD_PATH).executes(ResourceWorldModule::deleteWorld))
                                .then(literal(DEFAULT_THE_NETHER_PATH).executes(ResourceWorldModule::deleteWorld))
                                .then(literal(DEFAULT_THE_END_PATH).executes(ResourceWorldModule::deleteWorld)))
                        .then(literal("tp").then(literal(DEFAULT_OVERWORLD_PATH).executes(ResourceWorldModule::teleportWorld))
                                .then(literal(DEFAULT_THE_NETHER_PATH).executes(ResourceWorldModule::teleportWorld))
                                .then(literal(DEFAULT_THE_END_PATH).executes(ResourceWorldModule::teleportWorld))
                        )
        );
    }

    private static int resetWorlds(CommandContext<ServerCommandSource> ctx) {
        resetWorlds(ctx.getSource().getServer());
        return 1;
    }

    private static void resetWorlds(MinecraftServer server) {
        // hook on: ServerWorldEvents.UNLOAD
        broadcast(server.getCommandSource(), "Start to reset resource worlds...");
        deleteWorld(server, DEFAULT_OVERWORLD_PATH);
        deleteWorld(server, DEFAULT_THE_NETHER_PATH);
        deleteWorld(server, DEFAULT_THE_END_PATH);
    }

    public static void loadWorlds(MinecraftServer server) {
        createWorld(server, DimensionTypes.OVERWORLD, DEFAULT_OVERWORLD_PATH);
        createWorld(server, DimensionTypes.THE_NETHER, DEFAULT_THE_NETHER_PATH);
        createWorld(server, DimensionTypes.THE_END, DEFAULT_THE_END_PATH);
    }

    private static DimensionOptions getDimensionOptionsByDimensionType(MinecraftServer server, RegistryKey<DimensionType> dimensionType) {
        Registry<DimensionOptions> registry = server.getCombinedDynamicRegistries().getCombinedRegistryManager().get(RegistryKeys.DIMENSION);
        if (dimensionType == DimensionTypes.OVERWORLD) {
            return registry.get(DimensionOptions.OVERWORLD);
        }
        if (dimensionType == DimensionTypes.THE_NETHER) {
            return registry.get(DimensionOptions.NETHER);
        }
        if (dimensionType == DimensionTypes.THE_END) {
            return registry.get(DimensionOptions.END);
        }
        return null;
    }

    public static RegistryKey<DimensionType> getDimensionTypeByPath(String path) {
        if (path.equals(DEFAULT_OVERWORLD_PATH)) return DimensionTypes.OVERWORLD;
        if (path.equals(DEFAULT_THE_NETHER_PATH)) return DimensionTypes.THE_NETHER;
        if (path.equals(DEFAULT_THE_END_PATH)) return DimensionTypes.THE_END;
        return null;
    }

    public static void feedback(ServerCommandSource source, String content) {
        source.sendFeedback(() -> Text.literal(content).formatted(Formatting.RED), false);
    }

    public static void broadcast(ServerCommandSource source, String content) {
        source.getServer().getPlayerManager().broadcast(Text.literal(content).formatted(Formatting.RED), false);

    }


    public static int createWorld(CommandContext<ServerCommandSource> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        String path = ctx.getNodes().get(2).getNode().getName();
        RegistryKey<DimensionType> dimensionType = getDimensionTypeByPath(path);
        createWorld(server, dimensionType, path);

        feedback(ctx.getSource(), String.format("Create resource world %s done.", path));
        return 1;
    }

    public static void createWorld(MinecraftServer server, RegistryKey<DimensionType> dimensionType, String path) {
        /* create the world */
        RegistryKey<World> worldRegistryKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(DEFAULT_WORLD_PREFIX, path));
        MinecraftServerAccessor serverAccess = (MinecraftServerAccessor) server;
        long seed = RandomSeed.getSeed();
        ServerWorld world = new MyServerWorld(server,
                Util.getMainWorkerExecutor(),
                ((MinecraftServerAccessor) server).getSession(),
                server.getSaveProperties().getMainWorldProperties(),
                worldRegistryKey,
                getDimensionOptionsByDimensionType(server, dimensionType),
                VoidWorldGenerationProgressListener.INSTANCE,
                false,
                BiomeAccess.hashSeed(seed),
                ImmutableList.of(),
                true,
                null);

        if (dimensionType == DimensionTypes.THE_END) {
            world.setEnderDragonFight(new EnderDragonFight(world, world.getSeed(), EnderDragonFight.Data.DEFAULT));
        }

        /* register the world */
        serverAccess.getWorlds().put(world.getRegistryKey(), world);
        server.sendMessage(Text.of("Create world -> " + world.getRegistryKey().toString()));
        ServerWorldEvents.LOAD.invoker().onWorldLoad(server, world);
    }

    private static ServerWorld getResourceWorldByPath(MinecraftServer server, String path) {
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(DEFAULT_WORLD_PREFIX, path));
        return server.getWorld(worldKey);
    }

    private static BlockPos createSafePlatform(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos.down(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().north(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().north().west(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().north().east(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().south(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().south().west(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().south().east(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().west(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos.down().east(), Blocks.BEDROCK.getDefaultState());
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        return pos;
    }

    public static int teleportWorld(CommandContext<ServerCommandSource> ctx) {
        String path = ctx.getNodes().get(2).getNode().getName();
        ServerWorld world = getResourceWorldByPath(ctx.getSource().getServer(), path);
        if (world == null) {
            feedback(ctx.getSource(), String.format("Target resource world %s doesn't exist.", path));
            return 0;
        }

        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        BlockPos destPos;
        float destYaw = 90;
        float destPitch = 0;
        if (world.getDimensionKey() == DimensionTypes.THE_END) {
            ServerWorld.createEndSpawnPlatform(world);
            destPos = ServerWorld.END_SPAWN_POS;
        } else {
            destPos = createSafePlatform(world, new BlockPos(0, 96, 0));
        }

        player.teleport(world, destPos.getX() + 0.5, destPos.getY(), destPos.getZ() + 0.5, destYaw, destPitch);
        return 1;
    }


    public static void deleteWorld(MinecraftServer server, String path) {
        ServerWorld world = getResourceWorldByPath(server, path);
        if (world == null) return;

        MyWorldManager.enqueueWorldDeletion(world);
    }


    public static int deleteWorld(CommandContext<ServerCommandSource> ctx) {
        String path = ctx.getNodes().get(2).getNode().getName();
        ServerWorld world = getResourceWorldByPath(ctx.getSource().getServer(), path);
        if (world == null) {
            feedback(ctx.getSource(), String.format("Target resource world %s doesn't exist.", path));
            return 0;
        }

        deleteWorld(ctx.getSource().getServer(), path);
        feedback(ctx.getSource(), String.format("Delete resource world %s done.", path));
        return 1;
    }


    public static void onWorldUnload(MinecraftServer server, ServerWorld world) {
        if (server.isRunning()) {
            String namespace = world.getRegistryKey().getValue().getNamespace();
            String path = world.getRegistryKey().getValue().getPath();
            if (!namespace.equals(DEFAULT_WORLD_PREFIX)) return;

            server.sendMessage(Text.of(String.format("UNLOAD event: create world %s", path)));
            ResourceWorldModule.createWorld(server, ResourceWorldModule.getDimensionTypeByPath(path), path);
        }
    }
}
