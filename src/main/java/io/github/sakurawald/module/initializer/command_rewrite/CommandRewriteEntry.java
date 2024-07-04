package io.github.sakurawald.module.initializer.command_rewrite;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommandRewriteEntry {
    public String from;
    public String to;
}
