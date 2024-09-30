package io.github.sakurawald.module.initializer.color.sign;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.NbtHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.SpatialBlock;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.color.sign.structure.SignCache;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class SignInitializer extends ModuleInitializer {

    private static final String ATTACHMENT_SUBJECT = "color-sign-cache";

    public static @Nullable SignCache readSignCache(SpatialBlock spatialBlock) {
        String uuid = NbtHelper.computeUuid(spatialBlock);

        if (!Managers.getAttachmentManager().existsAttachmentFile(ATTACHMENT_SUBJECT, uuid)) {
            return null;
        }

        try {
            String data = Managers.getAttachmentManager().getAttachment(ATTACHMENT_SUBJECT, uuid);
            return BaseConfigurationHandler.getGson().fromJson(data, SignCache.class);
        } catch (IOException e) {
            LogUtil.error("failed to read sign cache: spatialBlock = {}", spatialBlock, e);
            return null;
        }
    }

    public static void writeSignCache(SpatialBlock spatialBlock, SignCache signCache) {
        String uuid = NbtHelper.computeUuid(spatialBlock);
        String data = BaseConfigurationHandler.getGson().toJson(signCache);
        try {
            Managers.getAttachmentManager().setAttachment(ATTACHMENT_SUBJECT, uuid, data);
        } catch (IOException e) {
            LogUtil.error("failed to write sign cache: spatialBlock = {}, signCache = {}", spatialBlock, signCache, e);
        }
    }

}
