package io.github.sakurawald.core.command.argument.wrapper.impl;

import com.mojang.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class GameProfileCollection {
    Collection<GameProfile> collection;
}
