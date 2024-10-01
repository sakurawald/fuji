package io.github.sakurawald.module.initializer.command_bundle;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.command.structure.CommandDescriptor;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_bundle.config.model.CommandBundleConfigModel;
import io.github.sakurawald.module.initializer.command_bundle.structure.DynamicCommandDescriptor;
import lombok.SneakyThrows;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@CommandNode("command-bundle")
@CommandRequirement(level = 4)
public class CommandBundleInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<CommandBundleConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON,CommandBundleConfigModel.class);

    public static int method(@NotNull List<Object> args) {
        LogUtil.debug("method() invoked with args: ");
        args.forEach(it -> LogUtil.debug("arg: {}", it));

        return 1;
    }

    @SneakyThrows
    @CommandNode("register")
    private static int register(@CommandSource ServerPlayerEntity player) {

        player.sendMessage(Text.of("run"));

        Method method = CommandBundleInitializer.class.getMethod("method", List.class);
        List<Argument> arguments = new ArrayList<>();

        // push the command source
        arguments.add(Argument.makeRequiredArgument(ServerPlayerEntity.class, "command-source", 0, false, null).markAsCommandSource());

        // push the args
        arguments.add(Argument.makeLiteralArgument("my-command", null));
        arguments.add(Argument.makeRequiredArgument(String.class, "str-arg-name", 1, false, null));
        arguments.add(Argument.makeRequiredArgument(BlockPos.class, "blockpos-arg-name", 2, false, null));
        arguments.add(Argument.makeRequiredArgument(Integer.class, "int-arg-name", 3, false, null));

        CommandDescriptor descriptor = new DynamicCommandDescriptor(method, arguments);
        descriptor.register();


        return CommandHelper.Return.SUCCESS;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
    }
}
