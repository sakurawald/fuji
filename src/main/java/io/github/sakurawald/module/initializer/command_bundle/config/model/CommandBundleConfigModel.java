package io.github.sakurawald.module.initializer.command_bundle.config.model;

import io.github.sakurawald.module.initializer.command_bundle.structure.CommandBundleEntry;
import io.github.sakurawald.core.command.structure.CommandRequirementDescriptor;

import java.util.ArrayList;
import java.util.List;

public class CommandBundleConfigModel {

    List<CommandBundleEntry> entries = new ArrayList<>() {
        {
            new CommandBundleEntry(new CommandRequirementDescriptor(4, ""), "my-command <int int-arg-name> [str str-arg-name]", List.of("say int is $int-arg-name", "say str is $str-arg-name"));
        }
    };

}
