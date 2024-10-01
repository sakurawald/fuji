package io.github.sakurawald.module.initializer.command_bundle.config.model;

import io.github.sakurawald.core.command.structure.CommandRequirementDescriptor;
import io.github.sakurawald.module.initializer.command_bundle.structure.BundleCommandEntry;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommandBundleConfigModel {

    List<BundleCommandEntry> entries = new ArrayList<>() {
        {
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-first-command <int int-arg-name> [str str-arg-name this is the default value]", List.of("say hello %player:name%", "say int is $int-arg-name", "say str is $str-arg-name")));
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-second-command first-literal second-literal <str str-arg-name>", List.of("say hello %player:name%", "say str is $str-arg-name")));
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-third-command <int int-arg-name> first-literal [str str-arg-name this is the default value]", List.of("say hello %player:name%", "say int is $int-arg-name", "say str is $str-arg-name")));
        }
    };

}
