package io.github.sakurawald.module.initializer.profiler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.gc.GarbageCollector;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ProfilerModule extends ModuleInitializer {
    public String formatBytes(long bytes) {
        if (bytes == -1) return "N/A";
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2fK", (double) bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2fM", (double) bytes / (1024 * 1024));
        } else {
            return String.format("%.2fG", (double) bytes / (1024 * 1024 * 1024));
        }
    }

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("profiler").executes(this::$profiler));
    }

    private int $profiler(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        CompletableFuture.runAsync(() -> {
            /* get instance */
            Spark spark = null;
            try {
                spark = SparkProvider.get();
            } catch (Exception ignored) {
            }
            if (spark == null) {
                MessageUtil.sendMessage(source, "profiler.spark.no_instance");
                return;
            }

            /* format */
            String os_name = ManagementFactory.getOperatingSystemMXBean().getName();
            String os_version = ManagementFactory.getOperatingSystemMXBean().getVersion();
            String os_arch = ManagementFactory.getOperatingSystemMXBean().getArch();

            String vmName = ManagementFactory.getRuntimeMXBean().getVmName();
            String vmVersion = ManagementFactory.getRuntimeMXBean().getVmVersion();

            double tps_5s = 0;
            double tps_10s = 0;
            double tps_1m = 0;
            double tps_5m = 0;
            double tps_15m = 0;
            DoubleStatistic<StatisticWindow.TicksPerSecond> tps = spark.tps();
            if (tps != null) {
                tps_5s = tps.poll(StatisticWindow.TicksPerSecond.SECONDS_5);
                tps_10s = tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10);
                tps_1m = tps.poll(StatisticWindow.TicksPerSecond.MINUTES_1);
                tps_5m = tps.poll(StatisticWindow.TicksPerSecond.MINUTES_5);
                tps_15m = tps.poll(StatisticWindow.TicksPerSecond.MINUTES_15);
            }

            GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> mspt = spark.mspt();

            double mspt_10s_min = 0;
            double mspt_10s_median = 0;
            double mspt_10s_95percentile = 0;
            double mspt_10s_max = 0;
            double mspt_1m_min = 0;
            double mspt_1m_median = 0;
            double mspt_1m_95percentile = 0;
            double mspt_1m_max = 0;
            if (mspt != null) {
                DoubleAverageInfo mspt_10s = mspt.poll(StatisticWindow.MillisPerTick.SECONDS_10);
                DoubleAverageInfo mspt_1m = mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1);
                mspt_10s_min = mspt_10s.min();
                mspt_10s_median = mspt_10s.median();
                mspt_10s_95percentile = mspt_10s.percentile(0.95);
                mspt_10s_max = mspt_10s.max();
                mspt_1m_min = mspt_1m.min();
                mspt_1m_median = mspt_1m.median();
                mspt_1m_95percentile = mspt_1m.percentile(0.95);
                mspt_1m_max = mspt_1m.max();
            }


            DoubleStatistic<StatisticWindow.CpuUsage> cpuProcess = spark.cpuProcess();
            DoubleStatistic<StatisticWindow.CpuUsage> cpuSystem = spark.cpuSystem();
            double cpu_process_10s = cpuProcess.poll(StatisticWindow.CpuUsage.SECONDS_10) * 100;
            double cpu_process_1m = cpuProcess.poll(StatisticWindow.CpuUsage.MINUTES_1) * 100;
            double cpu_process_15m = cpuProcess.poll(StatisticWindow.CpuUsage.MINUTES_15) * 100;
            double cpu_system_10s = cpuSystem.poll(StatisticWindow.CpuUsage.SECONDS_10) * 100;
            double cpu_system_1m = cpuSystem.poll(StatisticWindow.CpuUsage.MINUTES_1) * 100;
            double cpu_system_15m = cpuSystem.poll(StatisticWindow.CpuUsage.MINUTES_15) * 100;

            Map<String, GarbageCollector> gc = spark.gc();
            Component gcComponent = MessageUtil.ofComponent(source, "profiler.format.gc.head");
            int i = 0;
            for (GarbageCollector garbageCollector : gc.values()) {
                String name = garbageCollector.name();
                double avgFrequency = (double) garbageCollector.avgFrequency() / 1000;
                double avgTime = garbageCollector.avgTime();
                long totalCollections = garbageCollector.totalCollections();
                long totalTime = garbageCollector.totalTime();
                gcComponent = gcComponent.append(MessageUtil.ofComponent(source, i == gc.values().size() - 1 ? "profiler.format.gc.last" : "profiler.format.gc.no_last", name, avgFrequency, avgTime, totalCollections, totalTime));
                i++;
            }

            Component memComponent = MessageUtil.ofComponent(source, "profiler.format.mem.head");
            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            i = 0;
            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                String name = memoryPoolMXBean.getName();
                MemoryType type = memoryPoolMXBean.getType();
                MemoryUsage memoryUsage = memoryPoolMXBean.getUsage();
                String init = formatBytes(memoryUsage.getInit());
                String used = formatBytes(memoryUsage.getUsed());
                String committed = formatBytes(memoryUsage.getCommitted());
                String max = formatBytes(memoryUsage.getMax());
                memComponent = memComponent.append(MessageUtil.ofComponent(source, i == memoryPoolMXBeans.size() - 1 ? "profiler.format.mem.last" : "profiler.format.mem.no_last", name, type, init, used, committed, max));
                i++;
            }

            /* output */
            Component formatComponent = MessageUtil.ofComponent(source, "profiler.format"
                    , os_name, os_version, os_arch
                    , vmName, vmVersion
                    , tps_5s, tps_10s, tps_1m, tps_5m, tps_15m
                    , mspt_10s_min, mspt_10s_median, mspt_10s_95percentile, mspt_10s_max, mspt_1m_min, mspt_1m_median, mspt_1m_95percentile, mspt_1m_max
                    , cpu_system_10s, cpu_system_1m, cpu_system_15m, cpu_process_10s, cpu_process_1m, cpu_process_15m);
            source.sendMessage(formatComponent.appendNewline().append(memComponent).appendNewline().append(gcComponent));
        });

        return Command.SINGLE_SUCCESS;
    }
}
