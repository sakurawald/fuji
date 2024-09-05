package io.github.sakurawald.core.command.argument.wrapper.impl;

import io.github.sakurawald.core.command.argument.wrapper.abst.SingularValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.world.ServerWorld;

import java.lang.annotation.Retention;


public class Dimension extends SingularValue<ServerWorld> {

    public Dimension(ServerWorld value) {
        super(value);
    }
}
