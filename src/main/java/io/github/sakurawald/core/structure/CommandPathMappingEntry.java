package io.github.sakurawald.core.structure;

import lombok.Data;

import java.util.List;

@SuppressWarnings("unused")
@Data
public class CommandPathMappingEntry {
    final List<String> from;
    final List<String> to;
}
