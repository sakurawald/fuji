package fun.sakurawald.config;

import java.util.HashMap;

public class ChatGson {
    public HashMap<String, String> player2format = new HashMap<>() {
        {
            this.put("SakuraWald", "<rainbow>%message%");
        }
    };
}
