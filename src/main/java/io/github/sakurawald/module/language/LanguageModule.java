package io.github.sakurawald.module.language;

import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;


public class LanguageModule extends AbstractModule {

    @Override
    public void onReload() {
        MessageUtil.getLang2json().clear();
    }

}
