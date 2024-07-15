package io.github.sakurawald.module.initializer.works.work_type;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.works.ScheduleMethod;
import io.github.sakurawald.module.initializer.works.WorksCache;
import io.github.sakurawald.module.initializer.works.gui.ConfirmGui;
import io.github.sakurawald.module.initializer.works.gui.InputSignGui;
import io.github.sakurawald.module.mixin.top_chunks.ThreadedAnvilChunkStorageMixin;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.TextReplacementConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@NoArgsConstructor

public class ProductionWork extends Work implements ScheduleMethod {

    public @NotNull Sample sample = new Sample();

    public ProductionWork(ServerPlayerEntity player, String name) {
        super(player, name);
    }

    @Override
    protected String getType() {
        return WorkTypeAdapter.WorkType.ProductionWork.name();
    }

    private List<Text> formatSampleCounter(ServerPlayerEntity player) {
        List<Text> ret = new ArrayList<>();
        long currentTimeMS = System.currentTimeMillis();

        Stream<Map.Entry<String, Long>> sortedStream = this.sample.sampleCounter.entrySet().stream().sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        sortedStream.forEach(entry -> {
            String key = entry.getKey();
            double rate = entry.getValue() * ((double) (3600 * 1000) / ((Math.min(this.sample.sampleEndTimeMS, currentTimeMS)) - this.sample.sampleStartTimeMS));
            net.kyori.adventure.text.Component component = MessageUtil.ofComponent(player, "works.production_work.prop.sample_counter.entry", entry.getValue(), rate)
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[item]").replacement(Text.translatable(key)).build());
            ret.add(MessageUtil.toVomponent(component));
        });

        if (ret.isEmpty()) {
            ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_counter.empty"));
        }
        return ret;
    }

    @Override
    public List<Text> asLore(ServerPlayerEntity player) {
        /* construct lore */
        List<Text> ret = super.asLore(player);
        // note: hide sample info in lore if sample not exists
        if (this.sample.sampleStartTimeMS == 0) {
            ret.addAll((MessageUtil.ofVomponents(player, "works.production_work.sample.not_exists")));
            return ret;
        }

        ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_start_time", DateUtil.toStandardDateFormat(this.sample.sampleStartTimeMS)));
        ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_end_time", DateUtil.toStandardDateFormat(this.sample.sampleEndTimeMS)));
        ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_dimension", this.sample.sampleDimension));
        ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_coordinate", this.sample.sampleX, this.sample.sampleY, this.sample.sampleZ));
        ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_distance", this.sample.sampleDistance));

        // check npe to avoid broken
        if (this.sample.sampleCounter != null) {
            // trim counter
            if (this.sample.sampleCounter.size() > Configs.configHandler.model().modules.works.sample_counter_top_n) {
                trimCounter();
            }
            ret.add(MessageUtil.ofText(player, "works.production_work.prop.sample_counter"));
            ret.addAll(formatSampleCounter(player));
        }
        return ret;
    }

    @Override
    protected String getDefaultIcon() {
        return "minecraft:redstone";
    }

    public void openInputSampleDistanceGui(ServerPlayerEntity player) {
        new InputSignGui(player, MessageUtil.getString(player, "works.production_work.prompt.input.sample_distance")) {
            @Override
            public void onClose() {
                int limit = Configs.configHandler.model().modules.works.sample_distance_limit;
                int current;
                try {
                    current = Integer.parseInt(this.getLine(0).getString());
                } catch (NumberFormatException e) {
                    MessageUtil.sendActionBar(player, "input.syntax.error");
                    return;
                }

                if (current > limit) {
                    MessageUtil.sendActionBar(player, "input.limit.error");
                    return;
                }

                // set sample distance
                sample.sampleDistance = current;

                // start/restart sample
                if (isSampling()) {
                    endSample();
                }
                startSample(player);
            }
        }.open();
    }

    @Override
    public void openSpecializedSettingsGui(ServerPlayerEntity player, SimpleGui parentGui) {
        final SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
        gui.setTitle(MessageUtil.ofText(player, "works.work.set.specialized_settings.title"));
        gui.setLockPlayerInventory(true);
        gui.addSlot(new GuiElementBuilder()
                .setItem(Items.CLOCK)
                .setName(MessageUtil.ofText(player, "works.production_work.set.sample"))
                .setLore(MessageUtil.ofVomponents(player, "works.production_work.set.sample.lore"))
                .setCallback(() -> new ConfirmGui(player) {
                            @Override
                            public void onConfirm() {
                                openInputSampleDistanceGui(player);
                            }
                        }.open()
                )
        );
        gui.setSlot(8, new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setSkullOwner(GuiUtil.PREVIOUS_PAGE_ICON)
                .setName(MessageUtil.ofText(player, "back"))
                .setCallback(parentGui::open)
        );

        gui.open();
    }

    public boolean isSampling() {
        return System.currentTimeMillis() < this.sample.sampleEndTimeMS;
    }

    @Override
    public Item asItem() {
        return super.asItem();
    }

    private boolean insideSampleDistance(BlockPos position, BlockPos blockPos) {
        float deltaX = Math.abs(blockPos.getX() - position.getX());
        float deltaZ = Math.abs(blockPos.getZ() - position.getZ());
        return deltaX <= this.sample.sampleDistance && deltaZ <= this.sample.sampleDistance;
    }

    @SuppressWarnings("unused")
    private String formatBlockPosList(ArrayList<BlockPos> blockPosList) {
        StringBuilder sb = new StringBuilder();
        for (BlockPos blockPos : blockPosList) {
            sb.append("(").append(blockPos.getX()).append(",").append(blockPos.getY()).append(",").append(blockPos.getZ()).append(")").append(" ");
        }
        return sb.toString();
    }

    public int resolveHoppers(ServerPlayerEntity player) {
        // clear cache entry
        WorksCache.unbind(this);

        // add cache entry
        int hopperBlockCount = 0;
        int minecartHopperCount = 0;
        ServerWorld world = player.getServerWorld();
        ThreadedAnvilChunkStorageMixin threadedAnvilChunkStorage = (ThreadedAnvilChunkStorageMixin) world.getChunkManager().chunkLoadingManager;
        Iterable<ChunkHolder> chunkHolders = threadedAnvilChunkStorage.$getChunks();
        for (ChunkHolder chunkHolder : chunkHolders) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk == null) continue;
            /* count for block entities */
            for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                // improve: check type first for performance
                if (blockEntity instanceof HopperBlockEntity) {
                    if (insideSampleDistance(player.getBlockPos(), blockEntity.getPos())) {
                        WorksCache.bind(blockEntity.getPos(), this);
                        hopperBlockCount++;
                    }
                }
            }
        }
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof HopperMinecartEntity) {
                if (insideSampleDistance(player.getBlockPos(), entity.getBlockPos())) {
                    WorksCache.bind(entity.getId(), this);
                    minecartHopperCount++;
                }
            }
        }

        MessageUtil.sendMessage(player, "works.production_work.sample.resolve_hoppers.response", hopperBlockCount, minecartHopperCount);
        return hopperBlockCount + minecartHopperCount;
    }

    @Override
    public void onSchedule() {
        if (System.currentTimeMillis() >= this.sample.sampleEndTimeMS) {
            this.endSample();
        }
    }

    public void startSample(ServerPlayerEntity player) {
        this.sample.sampleStartTimeMS = System.currentTimeMillis();
        this.sample.sampleEndTimeMS = this.sample.sampleStartTimeMS + Configs.configHandler.model().modules.works.sample_time_ms;
        this.sample.sampleDimension = player.getServerWorld().getRegistryKey().getValue().toString();
        this.sample.sampleX = player.getX();
        this.sample.sampleY = player.getY();
        this.sample.sampleZ = player.getZ();
        this.sample.sampleCounter = new HashMap<>();
        if (this.resolveHoppers(player) == 0) {
            MessageUtil.sendMessage(player, "operation.cancelled");
            return;
        }

        MessageUtil.sendBroadcast("works.production_work.sample.start", name, this.creator);
    }

    public void endSample() {
        // unbind all block pos
        WorksCache.unbind(this);
        MessageUtil.sendBroadcast("works.production_work.sample.end", this.name, this.creator);

        // trim counter to avoid spam
        trimCounter();
    }

    public void trimCounter() {
        List<Map.Entry<String, Long>> sortedEntries = this.sample.sampleCounter.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .toList();

        int N = Configs.configHandler.model().modules.works.sample_counter_top_n;
        this.sample.sampleCounter.clear();
        for (int i = 0; i < N && i < sortedEntries.size(); i++) {
            this.sample.sampleCounter.put(sortedEntries.get(i).getKey(), sortedEntries.get(i).getValue());
        }
    }

    public void addCounter(ItemStack itemStack) {
        HashMap<String, Long> counter = this.sample.sampleCounter;
        String key = itemStack.getTranslationKey();
        counter.put(key, counter.getOrDefault(key, 0L) + itemStack.getCount());
    }

    public static class Sample {
        public String sampleDimension;
        public double sampleX;
        public double sampleY;
        public double sampleZ;
        public long sampleStartTimeMS;
        public long sampleEndTimeMS;
        public int sampleDistance;
        public HashMap<String, Long> sampleCounter;
    }
}
