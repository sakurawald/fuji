package io.github.sakurawald.util;

import io.github.sakurawald.Fuji;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

@UtilityClass
public class LogUtil {
    public static Logger createLogger(String name) {
        Logger logger = LogManager.getLogger(name);
        try {
            String level = System.getProperty("%s.level".formatted(Fuji.MOD_ID));
            Configurator.setLevel(logger, Level.getLevel(level));
        } catch (Exception e) {
            return logger;
        }
        return logger;
    }
}
