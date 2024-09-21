package io.github.sakurawald.module.initializer.motd;

import com.google.common.base.Preconditions;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.motd.config.model.MotdConfigModel;
import lombok.Setter;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class MotdInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<MotdConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, MotdConfigModel.class);

    private static final File ICON_FOLDER = ReflectionUtil.getModuleConfigPath(MotdInitializer.class).resolve("motd").resolve("icon").toFile();

    @Setter
    private static @NotNull List<String> motd = new ArrayList<>();

    @Override
    public void onInitialize() {
        setMotd(config.getModel().list);
    }

    @Override
    public void onReload() {
        setMotd(config.getModel().list);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static @NotNull Optional<ServerMetadata.Favicon> getRandomIcon() {
        ICON_FOLDER.mkdirs();
        File[] icons = ICON_FOLDER.listFiles();
        if (icons == null || icons.length == 0) {
            LogUtil.warn("no icons found in {}", ICON_FOLDER.getAbsolutePath());
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
            LogUtil.warn("failed to encode favicon", e);
            return Optional.empty();
        }
        return Optional.of(new ServerMetadata.Favicon(byteArrayOutputStream.toByteArray()));
    }

    public static @NotNull Text getRandomDescription() {
        return LocaleHelper.getTextByValue(null,motd.get(new Random().nextInt(motd.size())));
    }

}
