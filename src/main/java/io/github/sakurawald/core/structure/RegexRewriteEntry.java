package io.github.sakurawald.core.structure;

import lombok.Data;

@SuppressWarnings("unused")
@Data
public class RegexRewriteEntry {
    final String regex;
    final String replacement;
}
