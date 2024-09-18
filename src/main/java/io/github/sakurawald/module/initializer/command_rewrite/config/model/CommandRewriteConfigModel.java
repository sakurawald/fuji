package io.github.sakurawald.module.initializer.command_rewrite.config.model;

import io.github.sakurawald.core.structure.RegexRewriteEntry;

import java.util.ArrayList;
import java.util.List;

public class CommandRewriteConfigModel {
    public List<RegexRewriteEntry> regex = new ArrayList<>() {
        {
            this.add(new RegexRewriteEntry("home", "home tp default"));
        }
    };
}
