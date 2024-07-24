package io.github.sakurawald.module.common.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegexRewriteEntry {
    public String regex;
    public String replacement;
}
