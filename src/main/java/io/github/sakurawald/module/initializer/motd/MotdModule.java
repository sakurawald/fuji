package io.github.sakurawald.module.initializer.motd;

import com.google.common.base.Preconditions;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import javax.imageio.ImageIO;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class MotdModule extends ModuleInitializer {
    private final File ICON_FOLDER = Fuji.CONFIG_PATH.resolve("icon").toFile();

    private List<String> descriptions = new ArrayList<>();

    public void updateDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public void onInitialize() {
        updateDescriptions(Configs.configHandler.model().modules.motd.descriptions);
    }

    @Override
    public void onReload() {
        updateDescriptions(Configs.configHandler.model().modules.motd.descriptions);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Optional<ServerMetadata.Favicon> getRandomIcon() {
        ICON_FOLDER.mkdirs();
        File[] icons = ICON_FOLDER.listFiles();
        if (icons == null || icons.length == 0) {
            Fuji.LOGGER.warn("No icons found in {}", ICON_FOLDER.getAbsolutePath());
            return Optional.empty();
        }
        File randomIcon = icons[new Random().nextInt(icons.length)];
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            BufferedImage bufferedImage = ImageIO.read(randomIcon);
            Preconditions.checkState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
            Preconditions.checkState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        } catch (IOException e) {
            Fuji.LOGGER.warn("Failed to encode favicon", e);
            return Optional.empty();
        }
        return Optional.of(new ServerMetadata.Favicon(byteArrayOutputStream.toByteArray()));
    }

    public Text getRandomDescription() {
        return MessageUtil.ofVomponent(descriptions.get(new Random().nextInt(descriptions.size())));
    }


}
