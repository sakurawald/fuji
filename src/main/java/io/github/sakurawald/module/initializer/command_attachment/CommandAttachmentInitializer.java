package io.github.sakurawald.module.initializer.command_attachment;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.NbtHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.ExecuteAsType;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.InteractType;
import io.github.sakurawald.module.initializer.command_attachment.config.model.CommandAttachmentModel;
import io.github.sakurawald.module.initializer.command_attachment.structure.CommandAttachmentEntry;
import lombok.SneakyThrows;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CommandNode("command-attachment")
@CommandRequirement(level = 4)
public class CommandAttachmentInitializer extends ModuleInitializer {

    private static final String COMMAND_ATTACHMENT_SUBJECT_NAME = "command-attachment";

    @SneakyThrows
    private CommandAttachmentModel getModel(String uuid) {

        CommandAttachmentModel model;
        try {
            String attachment = Managers.getAttachmentManager().getAttachment(COMMAND_ATTACHMENT_SUBJECT_NAME, uuid);
            model = ConfigHandler.getGson().fromJson(attachment, CommandAttachmentModel.class);
        } catch (IOException e) {
            model = new CommandAttachmentModel();
            String json = ConfigHandler.getGson().toJson(model);
            Managers.getAttachmentManager().setAttachment(COMMAND_ATTACHMENT_SUBJECT_NAME, uuid, json);
        }

        return model;
    }

    @SneakyThrows
    private void setModel(String uuid, CommandAttachmentModel model) {
        String json = ConfigHandler.getGson().toJson(model);
        Managers.getAttachmentManager().setAttachment(COMMAND_ATTACHMENT_SUBJECT_NAME, uuid, json);
    }

    public void trigger(String uuid, ServerPlayerEntity player, List<InteractType> receivedInteractTypes) {
        // get
        CommandAttachmentModel model = this.getModel(uuid);

        // process
        for (CommandAttachmentEntry e : model.getEntries()) {
            if (!receivedInteractTypes.contains(e.getInteractType())) continue;
            if (e.getUseTimes() >= e.getMaxUseTimes()) continue;

            ExecuteAsType executeAsType = e.getExecuteAsType();
            switch (executeAsType) {
                case CONSOLE -> CommandExecutor.executeCommandAsConsole(player, e.getCommand());
                case PLAYER -> CommandExecutor.executeCommandAsPlayer(player, e.getCommand());
                case FAKE_OP -> CommandExecutor.executeCommandAsFakeOp(player,e.getCommand());
            }

            e.setUseTimes(e.getUseTimes() + 1);

            if (e.isDestroyItem() && e.getUseTimes() >= e.getMaxUseTimes()) {
                player.getMainHandStack().decrement(1);
            }
        }

        // save
        this.setModel(uuid, model);
    }


    // cooldown
    @CommandNode("attach-one")
    int attachOne(@CommandSource ServerPlayerEntity player
            , Optional<InteractType> interactType
            , Optional<Integer> maxUseTimes
            , Optional<ExecuteAsType> executeAsType
            , Optional<Boolean> destroyItem
            , GreedyString command
    ) {

        // get model
        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack.isEmpty()) {
            LanguageHelper.sendMessageByKey(player,"operation.fail");
            return CommandHelper.Return.FAIL;
        }

        String uuid = NbtHelper.getOrMakeUUIDNbt(mainHandStack);
        CommandAttachmentModel model = this.getModel(uuid);

        // new entry
        String $command = command.getValue();
        InteractType $interactType = interactType.orElse(InteractType.BOTH);
        ExecuteAsType $executeAsType = executeAsType.orElse(ExecuteAsType.FAKE_OP);
        Integer $maxUseTimes = maxUseTimes.orElse(Integer.MAX_VALUE);
        Boolean $destroyItem = destroyItem.orElse(true);

        model.getEntries().add(new CommandAttachmentEntry($command, $interactType, $executeAsType, $maxUseTimes, $destroyItem, 0));

        // save model
        this.setModel(uuid, model);

        LanguageHelper.sendMessageByKey(player, "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("detach-all")
    int detachAll(@CommandSource ServerPlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack.isEmpty()) {
            LanguageHelper.sendMessageByKey(player, "operation.fail");
            return CommandHelper.Return.FAIL;
        }

        String uuid = NbtHelper.getOrMakeUUIDNbt(mainHandStack);
        Managers.getAttachmentManager().unsetAttachment(COMMAND_ATTACHMENT_SUBJECT_NAME, uuid);

        LanguageHelper.sendMessageByKey(player, "operation.success");
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    @CommandNode("query")
    int query(@CommandSource ServerPlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack.isEmpty()) {
            LanguageHelper.sendMessageByKey(player, "operation.fail");
            return CommandHelper.Return.FAIL;
        }

        String uuid = NbtHelper.getUuid(mainHandStack.get(DataComponentTypes.CUSTOM_DATA));
        if (uuid == null) {
            LanguageHelper.sendMessageByKey(player, "command_attachment.query.no_attachment");
            return CommandHelper.Return.SUCCESS;
        }

        String attachment = Managers.getAttachmentManager().getAttachment(COMMAND_ATTACHMENT_SUBJECT_NAME, uuid);
        player.sendMessage(Text.literal(attachment));
        return CommandHelper.Return.SUCCESS;
    }
}
