package io.github.sakurawald.module.initializer.language;

import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.module.initializer.ModuleInitializer;


public class LanguageInitializer extends ModuleInitializer {

    @Override
    public void onReload() {
        MessageHelper.forgetLoadedLanguages();
    }

}
