package io.github.sakurawald.module.initializer.chat;

import com.mojang.datafixers.TypeRewriteRule;
import io.github.sakurawald.config.annotation.Documentation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegexEntry {
    String regex;
    String replacement;
}
