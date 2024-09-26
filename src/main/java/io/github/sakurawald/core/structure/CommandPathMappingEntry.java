package io.github.sakurawald.core.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommandPathMappingEntry {
    public List<String> from;
    public List<String> to;
}
