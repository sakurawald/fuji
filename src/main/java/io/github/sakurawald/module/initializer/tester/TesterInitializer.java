package io.github.sakurawald.module.initializer.tester;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;


@CommandNode("tester")
@CommandRequirement(level = 4)
public class TesterInitializer extends ModuleInitializer {

    @CommandNode("run")
    private static int $run(@CommandSource ServerPlayerEntity player) {

        return 1;
    }

}
