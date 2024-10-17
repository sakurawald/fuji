package io.github.sakurawald.module.initializer.chat.style.model;

import io.github.sakurawald.core.job.impl.MentionPlayersJob;
import io.github.sakurawald.core.structure.RegexRewriteNode;

import java.util.ArrayList;
import java.util.List;

public class ChatStyleConfigModel {

    public Style style = new Style();
    public static class Style {
        public String sender = "<#B1B2FF>[%fuji:player_playtime%\uD83D\uDD25 %fuji:player_mined%⛏ %fuji:player_placed%\uD83D\uDD33 %fuji:player_killed%\uD83D\uDDE1 %fuji:player_moved%\uD83C\uDF0D]<reset> <<dark_green><click:suggest_command:'/msg %player:name% '><hover:show_text:'Time: %fuji:date%<newline><italic>Click to Message'>%player:displayname_visual%</hover></click></dark_green>> "; // use emoji

        public String content = "%s";
    }

    public Rewrite rewrite = new Rewrite();
    public MentionPlayersJob.MentionPlayer mention_player = new MentionPlayersJob.MentionPlayer();
    public Spy spy = new Spy();

    public static class Rewrite {
        public List<RegexRewriteNode> regex = new ArrayList<>() {
            {
                this.add(new RegexRewriteNode("^BV(\\w{10})", "<underline><blue><hover:show_text:'$1'><click:open_url:'https://www.bilibili.com/video/BV$1'>bilibili $1</click></hover></blue></underline>"));
                this.add(new RegexRewriteNode("(?<=^|\\s)item(?=\\s|$)", "%fuji:item%"));
                this.add(new RegexRewriteNode("(?<=^|\\s)inv(?=\\s|$)", "%fuji:inv%"));
                this.add(new RegexRewriteNode("(?<=^|\\s)ender(?=\\s|$)", "%fuji:ender%"));
                this.add(new RegexRewriteNode("(?<=^|\\s)pos(?=\\s|$)", "%fuji:pos%"));
                this.add(new RegexRewriteNode("((https?)://[^\\s/$.?#].\\S*)", "<underline><blue><hover:show_text:'$1'><click:open_url:'$1'>$1</click></hover></blue></underline>"));
            }
        };
    }

    public static class Spy {
        public boolean output_unparsed_message_into_console = false;
    }
}
