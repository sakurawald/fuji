package io.github.sakurawald.module.initializer.command_meta.attachment;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper.SubjectId;
import io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper.SubjectName;
import lombok.SneakyThrows;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@CommandNode("attachment")
@CommandRequirement(level = 4)
public class AttachmentInitializer extends ModuleInitializer {

    @CommandNode("set")
    @SneakyThrows
    int set(@CommandSource CommandContext<ServerCommandSource> ctx, SubjectName subject, SubjectId uuid, GreedyString data) {
        Managers.getAttachmentManager().setAttachment(subject.getValue(), uuid.getValue(), data.getValue());

        MessageHelper.sendMessageByKey(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("unset")
    int unset(@CommandSource CommandContext<ServerCommandSource> ctx, SubjectName subject, SubjectId uuid) {
        boolean flag = Managers.getAttachmentManager().unsetAttachment(subject.getValue(), uuid.getValue());

        MessageHelper.sendMessageByKey(ctx.getSource(), flag ? "operation.success" : "operation.fail");
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    @CommandNode("get")
    int get(@CommandSource CommandContext<ServerCommandSource> ctx, SubjectName subject, SubjectId uuid) {
        String attachment = Managers.getAttachmentManager().getAttachment(subject.getValue(), uuid.getValue());

        ctx.getSource().sendMessage(Text.literal(attachment));
        return CommandHelper.Return.SUCCESS;
    }
}
