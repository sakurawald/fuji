package io.github.sakurawald.module.initializer.language;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;


public class LanguageInitializer extends ModuleInitializer {

    @Override
    public void onReload() {
        MessageHelper.forgetLoadedLanguages();
    }

}
