package fun.sakurawald.resource_world;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.ModMain;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.literal;

public class ResourceWorldModule {

    public static final String DEFAULT_WORLD_PREFIX = "resource_world";
    private static final String DEFAULT_OVERWORLD_PATH = "overworld";
    public static final String DEFAULT_THE_NETHER_PATH = "the_nether";
    public static final String DEFAULT_THE_END_PATH = "the_end";

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

    private static ChunkGenerator dimensionTypeToChunkGenerator(MinecraftServer server, RegistryKey<DimensionType> dimensionType) {
        if (dimensionType == DimensionTypes.OVERWORLD) {
            return server.getWorld(World.OVERWORLD).getChunkManager().getChunkGenerator();
        }
        if (dimensionType == DimensionTypes.THE_NETHER) {
            return server.getWorld(World.NETHER).getChunkManager().getChunkGenerator();
        }
        if (dimensionType == DimensionTypes.THE_END) {
            return server.getWorld(World.END).getChunkManager().getChunkGenerator();
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
        ChunkGenerator generator = dimensionTypeToChunkGenerator(server, dimensionType);
        RuntimeWorldConfig config = new RuntimeWorldConfig()
                .setDimensionType(dimensionType)
                .setDifficulty(Difficulty.HARD)
                .setSeed(RandomSeed.getSeed())
                .setGenerator(generator);

        Fantasy fantasy = Fantasy.get(server);
        fantasy.getOrOpenPersistentWorld(new Identifier(DEFAULT_WORLD_PREFIX, path), config);
    }

    private static ServerWorld getWorldByFullName(MinecraftServer server, String fullName) {
        ServerWorld world = null;
        for (RegistryKey<World> worldRegistryKey : server.getWorldRegistryKeys()) {
            String worldFullName = worldRegistryKey.getValue().toString();
            if (!worldFullName.equals(fullName)) continue;
            world = server.getWorld(worldRegistryKey);
            break;
        }
        return world;
    }

    private static ServerWorld getResourceWorldByPath(MinecraftServer server, String path) {
        return getWorldByFullName(server, DEFAULT_WORLD_PREFIX + ":" + path);
    }

    private static BlockPos createSafePlatform(ServerWorld world, BlockPos raw_pos) {
        BlockPos pos = raw_pos;
        world.setBlockState(pos.down(), Blocks.BEDROCK.getDefaultState());
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
        BlockPos destPos = null;
        float destYaw = 0;
        float destPitch = 0;
        if (world.getDimensionKey() == DimensionTypes.THE_END) {
            ServerWorld.createEndSpawnPlatform(world);
            destPos = ServerWorld.END_SPAWN_POS;
            destYaw = 90;
            destPitch = 0;
        } else {
            destPos = createSafePlatform(world, world.getSpawnPos());
        }

        player.teleport(world, destPos.getX(), destPos.getY(), destPos.getZ(), destYaw, destPitch);
        return 1;
    }

    public static void deleteWorld(MinecraftServer server, String path) {
        ServerWorld world = getResourceWorldByPath(server, path);
        if (world == null) return;

        Fantasy fantasy = Fantasy.get(server);
        RuntimeWorldHandle worldHandle = fantasy.getOrOpenPersistentWorld(new Identifier(DEFAULT_WORLD_PREFIX, path), null);
        worldHandle.delete();
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
            String path = world.getRegistryKey().getValue().getPath();
            server.sendMessage(Text.of(String.format("UNLOAD event: create world %s", path)));
            ResourceWorldModule.createWorld(server, ResourceWorldModule.getDimensionTypeByPath(path), path);
        }
    }
}
