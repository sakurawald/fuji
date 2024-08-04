package io.github.sakurawald.module.common.manager.impl.attachment;

import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.interfaces.AbstractManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

public class AttachmentManager extends AbstractManager {

    @Override
    public void onInitialize() {


    }

}
