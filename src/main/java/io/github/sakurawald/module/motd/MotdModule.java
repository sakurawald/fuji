package io.github.sakurawald.module.motd;

import com.google.common.base.Preconditions;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
public class MotdModule extends AbstractModule {
    private final File ICON_FOLDER = ServerMain.CONFIG_PATH.resolve("icon").toFile();

    private List<String> descriptions = new ArrayList<>();

    public void updateDescriptions(ArrayList<String> descriptions) {
        this.descriptions = descriptions;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Optional<ServerStatus.Favicon> getRandomIcon() {
        ICON_FOLDER.mkdirs();
        File[] icons = ICON_FOLDER.listFiles();
        if (icons == null || icons.length == 0) {
            log.warn("No icons found in {}", ICON_FOLDER.getAbsolutePath());
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
            log.warn("Failed to encode favicon", e);
            return Optional.empty();
        }
        return Optional.of(new ServerStatus.Favicon(byteArrayOutputStream.toByteArray()));
    }

    public Component getRandomDescription() {
        return MessageUtil.ofVomponentFromMiniMessage(descriptions.get(new Random().nextInt(descriptions.size())));
    }


}
