package io.github.sakurawald.config.model;

import java.util.HashMap;

public class NicknameModel {

    public Format format = new Format();
    public static class Format {
        public HashMap<String, String> player2format = new HashMap<>() {
            {
                this.put("Steve", "<rainbow>Steve");
            }
        };
    }
}
