package io.github.sakurawald.module.initializer.command_spy.config.model;

import java.util.ArrayList;
import java.util.List;

public class CommandSpyConfigModel {

    public List<String> ignore = new ArrayList<>() {
        {
            this.add("login.*");
        }
    };

    public boolean spy_on_console = false;
}
