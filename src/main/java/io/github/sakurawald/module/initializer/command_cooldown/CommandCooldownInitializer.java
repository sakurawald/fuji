package io.github.sakurawald.module.initializer.command_cooldown;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PlaceholderHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyStringList;
import io.github.sakurawald.core.command.argument.wrapper.impl.StringList;
import io.github.sakurawald.core.command.exception.AbortCommandExecutionException;
import io.github.sakurawald.core.command.executor.CommandExecutor;
import io.github.sakurawald.core.command.structure.ExtendedCommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.core.structure.CommandCooldown;
import io.github.sakurawald.core.structure.Cooldown;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_cooldown.command.argument.wrapper.CommandCooldownName;
import io.github.sakurawald.module.initializer.command_cooldown.config.model.CommandCooldownConfigModel;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CommandNode("command-cooldown")
@CommandRequirement(level = 4)
public class CommandCooldownInitializer extends ModuleInitializer {
    public static final BaseConfigurationHandler<CommandCooldownConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandCooldownConfigModel.class) {
        @Override
        public void beforeWriteStorage() {
            this.getModel().namedCooldown.list.values()
                .stream()
                .filter(it-> !it.isPersistent())
                // clear the timestamp for non-persistent cooldown before writing storage.
                .forEach(it->it.getTimestamp().clear());
        }
    };

    public static final MutableText NOT_COOLDOWN_FOUND = Text.literal("NOT_COOLDOWN_FOUND");

    private static final Map<String, Cooldown<String>> player2cooldown = new HashMap<>();

    @Override
    public void onInitialize() {
        config.scheduleSaveConfigurationHandlerJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void registerPlaceholder() {
        PlaceholderHelper.playerPlaceholder("command_cooldown_left_time", ((player, args) -> {
            CommandCooldown cooldown = config.getModel().namedCooldown.list.get(args);
            if (cooldown == null) return NOT_COOLDOWN_FOUND;

            String key = player.getGameProfile().getName();
            long leftTime = cooldown.computeCooldown(key, cooldown.getCooldownMs());
            leftTime = Math.max(0, leftTime);
            return Text.literal(String.valueOf(leftTime));
        }));

        PlaceholderHelper.playerPlaceholder("command_cooldown_left_usage", ((player, args) -> {
            CommandCooldown cooldown = config.getModel().namedCooldown.list.get(args);
            if (cooldown == null) return NOT_COOLDOWN_FOUND;

            String key = player.getGameProfile().getName();
            int usage = cooldown.getUsage().getOrDefault(key, 0);
            int leftUsage = cooldown.getMaxUsage() - usage;
            return Text.literal(String.valueOf(leftUsage));
        }));
    }

    public static long computeCooldown(ServerPlayerEntity player, @NotNull String commandLine) {
        String name = player.getGameProfile().getName();

        Cooldown<String> cooldown = player2cooldown.computeIfAbsent(name, k -> new Cooldown<>());

        Optional<Map.Entry<String, Long>> first = config.getModel().regex2ms.entrySet()
            .stream()
            .filter(it -> commandLine.matches(it.getKey()))
            .findFirst();

        return first.map(entry -> cooldown.tryUse(entry.getKey(), entry.getValue()))
            .orElse(-1L);
    }

    @CommandNode("test")
    private static int test(@CommandSource ServerCommandSource source
        , CommandCooldownName name
        , ServerPlayerEntity player
        , Optional<StringList> onFailed
        , GreedyStringList onSuccess
    ) {
        ensureExist(source, name);

        CommandCooldown cooldown = config.getModel().namedCooldown.list.get(name.getValue());
        StringList $onFailed = onFailed.orElse(new StringList(Collections.emptyList()));
        String key = player.getGameProfile().getName();

        /* test */
        long leftTime = cooldown.tryUse(key, cooldown.getCooldownMs());
        int usage = cooldown.getUsage().getOrDefault(key, 0);
        int leftUsage = cooldown.getMaxUsage() - usage;
        if (leftTime > 0 || leftUsage <= 0) {
            CommandExecutor.execute(ExtendedCommandSource.asConsole(player.getCommandSource()), $onFailed.getValue());
            return CommandHelper.Return.FAIL;
        }

        cooldown.getUsage().compute(key, (k, v) -> v == null ? 1 : v + 1);
        CommandExecutor.execute(ExtendedCommandSource.asConsole(player.getCommandSource()), onSuccess.getValue());
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("create")
    private static int create(@CommandSource ServerCommandSource source
        , String name
        , long cooldownMs
        , Optional<Integer> maxUsage
        , Optional<Boolean> persistent
        , Optional<Boolean> global) {
        ensureNotExist(source, name);

        int $maxUsage = maxUsage.orElse(Integer.MAX_VALUE);
        Boolean $persistent = persistent.orElse(true);
        Boolean $global = global.orElse(false);

        CommandCooldown commandCooldown = new CommandCooldown(name, cooldownMs, $maxUsage, $persistent, $global);
        config.getModel().namedCooldown.list.put(name, commandCooldown);

        LocaleHelper.sendMessageByKey(source, "created");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("delete")
    private static int delete(@CommandSource ServerCommandSource source, CommandCooldownName name) {
        ensureExist(source, name);

        String key = name.getValue();
        config.getModel().namedCooldown.list.remove(key);
        LocaleHelper.sendMessageByKey(source, "deleted");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("list")
    private static int list(@CommandSource ServerCommandSource source) {
        config.getModel().namedCooldown.list.keySet().forEach(it -> source.sendMessage(Text.literal(it)));
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("reset")
    private static int reset(@CommandSource ServerCommandSource source
        , CommandCooldownName name
        , ServerPlayerEntity player) {
        ensureExist(source, name);

        CommandCooldown commandCooldown = config.getModel().namedCooldown.list.get(name.getValue());

        String key = player.getGameProfile().getName();
        commandCooldown.getTimestamp().put(key, 0L);

        LocaleHelper.sendMessageByKey(source, "command_cooldown.reset", key, name.getValue());
        return CommandHelper.Return.SUCCESS;
    }

    private static void ensureExist(ServerCommandSource source, CommandCooldownName name) {
        if (!config.getModel().namedCooldown.list.containsKey(name.getValue())) {
            LocaleHelper.sendMessageByKey(source, "not_found");
            throw new AbortCommandExecutionException();
        }
    }

    private static void ensureNotExist(ServerCommandSource source, String name) {
        if (config.getModel().namedCooldown.list.containsKey(name)) {
            LocaleHelper.sendMessageByKey(source, "already_exists");
            throw new AbortCommandExecutionException();
        }
    }

}
