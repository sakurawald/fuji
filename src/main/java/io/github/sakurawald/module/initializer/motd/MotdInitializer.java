package io.github.sakurawald.module.initializer.motd;

import com.google.common.base.Preconditions;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.MessageHelper;
import javax.imageio.ImageIO;

import lombok.Setter;
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


public class MotdInitializer extends ModuleInitializer {
    private final File ICON_FOLDER = Fuji.CONFIG_PATH.resolve("motd").resolve("icon").toFile();

    @Setter
    private List<String> motd = new ArrayList<>();

    @Override
    public void onInitialize() {
        setMotd(Configs.configHandler.model().modules.motd.list);
    }

    @Override
    public void onReload() {
        setMotd(Configs.configHandler.model().modules.motd.list);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Optional<ServerMetadata.Favicon> getRandomIcon() {
        if (!Configs.configHandler.model().modules.motd.icon.enable) {
            return Optional.empty();
        }

        ICON_FOLDER.mkdirs();
        File[] icons = ICON_FOLDER.listFiles();
        if (icons == null || icons.length == 0) {
            LogUtil.warn("No icons found in {}", ICON_FOLDER.getAbsolutePath());
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
            LogUtil.warn("Failed to encode favicon", e);
            return Optional.empty();
        }
        return Optional.of(new ServerMetadata.Favicon(byteArrayOutputStream.toByteArray()));
    }

    public Text getRandomDescription() {
        return MessageHelper.ofText(motd.get(new Random().nextInt(motd.size())));
    }

}
