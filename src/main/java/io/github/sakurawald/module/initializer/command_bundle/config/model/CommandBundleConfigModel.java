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
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-command a-command-with-optional-arg <int int-arg-name> [str str-arg-name this is the default value]", List.of("say hello %player:name%", "say int is $int-arg-name", "say str is $str-arg-name")));
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-command a-command-with-literal-arg first-literal second-literal <str str-arg-name>", List.of("say hello %player:name%", "say str is $str-arg-name")));
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-command a-command-with-optional-arg-and-literal-arg <int int-arg-name> first-literal [str str-arg-name the default value can contains placeholder %player:name% in %world:name%]", List.of("say hello %player:name%", "say int is $int-arg-name", "say str is $str-arg-name")));
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(4, null), "my-command test-greedy <int int-arg-name> first-literal [greedy-string greedy-string-arg-name this is the default value]", List.of("say hello %player:name%", "say int is $int-arg-name", "say str is $str-arg-name")));
            this.add(new BundleCommandEntry(new CommandRequirementDescriptor(0, null), "introduce-me", List.of("run as player %player:name% me i am %player:name%")));
        }
    };

}
