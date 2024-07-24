package io.github.sakurawald.module.initializer.command_alias.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommandAliasEntry {
    public List<String> from;
    public List<String> to;
}
