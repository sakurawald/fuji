package io.github.sakurawald.module.initializer.command_warmup.config.model;

import io.github.sakurawald.module.initializer.command_warmup.structure.CommandWarmupEntry;
import io.github.sakurawald.core.manager.impl.bossbar.structure.Interruptible;

import java.util.ArrayList;
import java.util.List;

public class CommandWarmupConfigModel {

    public List<CommandWarmupEntry> entries = new ArrayList<>(){
        {
            this.add(new CommandWarmupEntry("back", 3 * 1000, new Interruptible(true, 3, true, true)));
        }
    };
}
