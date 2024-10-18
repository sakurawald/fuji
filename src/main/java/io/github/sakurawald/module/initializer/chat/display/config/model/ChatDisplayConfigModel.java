package io.github.sakurawald.module.initializer.chat.display.config.model;

public class ChatDisplayConfigModel {
    public int expiration_duration_s = 3600;

    public ReplaceToken replace_token = new ReplaceToken();
    public static class ReplaceToken {

        public String item_display_token = "[item]";
        public String inv_display_token = "[inv]";
        public String ender_display_token = "[ender]";
    }
}
