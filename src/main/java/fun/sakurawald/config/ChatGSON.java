package fun.sakurawald.config;

import java.util.HashMap;

@SuppressWarnings("InnerClassMayBeStatic")
public class ChatGSON {

    public Format format = new Format();

    public class Format {
        public HashMap<String, String> player2format = new HashMap<>() {
            {
                this.put("SakuraWald", "<rainbow>%message%");
            }
        };
    }
}
