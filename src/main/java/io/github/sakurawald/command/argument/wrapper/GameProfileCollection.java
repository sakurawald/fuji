package io.github.sakurawald.command.argument.wrapper;

import com.mojang.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class GameProfileCollection {
    Collection<GameProfile> collection;
}
