package io.github.sakurawald.module.initializer.command_meta.json;

import com.jayway.jsonpath.DocumentContext;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_meta.json.command.argument.wrapper.JsonValueType;
import lombok.SneakyThrows;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.BiFunction;

@CommandNode("json")
@CommandRequirement(level = 4)
public class JsonInitializer extends ModuleInitializer {

    @SneakyThrows
    void operateJson(String filePath, BiFunction<DocumentContext, Path, Boolean> function) {
        Path path = Path.of(filePath);
        DocumentContext documentContext = ConfigHandler.getJsonPathParser().parse(path.toFile());
        Boolean destructiveFlag = function.apply(documentContext, path);

        if (destructiveFlag) {
            String json = ConfigHandler.getGson().toJson(documentContext.json());
            try {
                FileUtils.write(path.toFile(), json, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @CommandNode("read")
    int read(@CommandSource CommandContext<ServerCommandSource> ctx, String filePath, String jsonPath) {
        operateJson(filePath, (documentContext, path) -> {
            Object read = documentContext.read(jsonPath);
            ctx.getSource().sendMessage(Text.literal(read.toString()));
            return false;
        });
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("write")
    int write(@CommandSource CommandContext<ServerCommandSource> ctx, String filePath, String jsonPath, JsonValueType valueType, GreedyString value) {
        operateJson(filePath, (documentContext, path) -> {
            Object obj = valueType.parse(value.getValue());
            documentContext.set(jsonPath, obj);
            return true;
        });

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("delete")
    int delete(@CommandSource CommandContext<ServerCommandSource> ctx, String filePath, String jsonPath) {
        operateJson(filePath, (documentContext, path) -> {
            documentContext.delete(jsonPath);
            return true;
        });

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("put")
    int put(@CommandSource CommandContext<ServerCommandSource> ctx, String filePath, String jsonPath, String jsonKey, JsonValueType valueType, GreedyString value) {
        operateJson(filePath, (documentContext, path) -> {
            Object obj = valueType.parse(value.getValue());
            documentContext.put(jsonPath, jsonKey, obj);
            return true;
        });

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("renameKey")
    int renameKey(@CommandSource CommandContext<ServerCommandSource> ctx, String filePath, String jsonPath, String oldJsonKey, String newJsonKey) {
        operateJson(filePath, (documentContext, path) -> {
            documentContext.renameKey(jsonPath, oldJsonKey, newJsonKey);
            return true;
        });

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }
}
