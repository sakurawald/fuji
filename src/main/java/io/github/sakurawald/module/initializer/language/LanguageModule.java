package io.github.sakurawald.module.initializer.language;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;


public class LanguageModule extends ModuleInitializer {

    @Override
    public void onReload() {
        MessageUtil.getLang2json().clear();
    }

}
