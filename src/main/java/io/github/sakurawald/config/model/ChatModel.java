package io.github.sakurawald.config.model;

import java.util.HashMap;

@SuppressWarnings("InnerClassMayBeStatic")
public class ChatModel {

    public Format format = new Format();

    public class Format {
        public HashMap<String, String> player2format = new HashMap<>() {
            {
                this.put("Steve", "<#FFC7EA>%message%");
            }
        };
    }
}
