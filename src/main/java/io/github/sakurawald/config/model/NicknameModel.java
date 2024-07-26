package io.github.sakurawald.config.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class NicknameModel {

    public @NotNull Format format = new Format();
    public static class Format {
        public @NotNull HashMap<String, String> player2format = new HashMap<>() {
            {
                this.put("Steve", "<rainbow>Steve");
            }
        };
    }
}
