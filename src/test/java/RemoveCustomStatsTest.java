import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sakurawald.ServerMain;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class RemoveCustomStatsTest {

    public static void main(String[] args) {
        ServerMain.log.info("{}", 0 / 8);
        ServerMain.log.info("{}", -2 / 8);
        ServerMain.log.info("{}", -6 / 8);
        ServerMain.log.info("{}", -9 / 8);
    }

    void removeStats() {
        File file = new File("run/world/stats/");
        File[] files = file.listFiles();
        if (files == null) return;

        Gson gson = new Gson();
        for (File playerStatFile : files) {
            try {
                // get player statistics
                JsonObject json = JsonParser.parseReader(new FileReader(playerStatFile)).getAsJsonObject();
                JsonObject stats = json.getAsJsonObject("stats");
                if (stats == null) continue;

                // sync player stat file
                JsonObject custom = stats.getAsJsonObject("minecraft:custom");
                custom.remove("minecraft:mined_all");
                custom.remove("minecraft:placed_all");
                custom.remove("minecraft:moved_all");

                try (FileWriter writer = new FileWriter(playerStatFile)) {
                    writer.write(gson.toJson(json));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
