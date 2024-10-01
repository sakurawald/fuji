package io.github.sakurawald.module.initializer.command_bundle.structure;

import com.mojang.brigadier.Command;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.command.exception.AbortCommandExecutionException;
import io.github.sakurawald.core.command.structure.CommandDescriptor;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class DynamicCommandDescriptor extends CommandDescriptor {

    public DynamicCommandDescriptor(Method method, List<Argument> arguments) {
        super(method, arguments);
    }

    @Override
    protected Command<ServerCommandSource> makeCommandFunctionClosure(CommandDescriptor descriptor) {
        return (ctx) -> {

            /* verify command source */
            if (!verifyCommandSource(ctx, descriptor)) {
                return CommandHelper.Return.FAIL;
            }

            /* invoke the command function */
            List<Object> args = makeCommandFunctionArgs(ctx, descriptor);

            int value;
            try {
                value = (int) descriptor.method.invoke(null, args);
            } catch (Exception e) {
                /* get the real exception during reflection. */
                Throwable theRealException = e;
                if (e instanceof InvocationTargetException) {
                    theRealException = e.getCause();
                }

                /* handle AbortCommandExecutionException */
                if (theRealException instanceof AbortCommandExecutionException) {
                    // the logging is done before throwing the AbortOperationException, here we just swallow this exception.
                    return CommandHelper.Return.FAIL;
                }

                /* report the exception */
                reportException(ctx.getSource(), descriptor.method, theRealException);
                return CommandHelper.Return.FAIL;
            }

            return value;
        };
    }
}
