package io.github.sakurawald.core.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegexRewriteEntry {
    public String regex;
    public String replacement;
}
