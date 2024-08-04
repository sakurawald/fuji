package io.github.sakurawald.module.initializer.command_meta.attachment;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper.SubjectId;
import io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper.SubjectName;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.SneakyThrows;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@Command("attachment")
@CommandPermission(level = 4)
public class AttachmentInitializer extends ModuleInitializer {

    @Command("set")
    @SneakyThrows
    int set(@CommandSource CommandContext<ServerCommandSource> ctx, SubjectName subject, SubjectId uuid, GreedyString data) {
        Managers.getAttachmentManager().setAttachment(subject.getName(), uuid.getUuid(), data.getString());

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @Command("unset")
    int unset(@CommandSource CommandContext<ServerCommandSource> ctx, SubjectName subject, SubjectId uuid) {
        boolean flag = Managers.getAttachmentManager().unsetAttachment(subject.getName(), uuid.getUuid());

        MessageHelper.sendMessage(ctx.getSource(), flag ? "operation.success" : "operation.fail");
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    @Command("get")
    int get(@CommandSource CommandContext<ServerCommandSource> ctx, SubjectName subject, SubjectId uuid) {
        String attachment = Managers.getAttachmentManager().getAttachment(subject.getName(), uuid.getUuid());

        ctx.getSource().sendMessage(Text.literal(attachment));
        return CommandHelper.Return.SUCCESS;
    }
}
