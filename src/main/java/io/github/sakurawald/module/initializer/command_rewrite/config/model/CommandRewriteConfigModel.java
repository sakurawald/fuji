package io.github.sakurawald.module.initializer.command_rewrite.config.model;

import com.google.gson.annotations.SerializedName;
import io.github.sakurawald.core.structure.RegexRewriteEntry;

import java.util.ArrayList;
import java.util.List;

public class CommandRewriteConfigModel {

    @SerializedName(value = "rewrite", alternate = "regex")
    public List<RegexRewriteEntry> rewrite = new ArrayList<>() {
        {
            this.add(new RegexRewriteEntry("home", "home tp default"));
        }
    };
}
