package io.github.sakurawald.module;

import java.util.function.Supplier;

public abstract class AbstractModule {

    public abstract Supplier<Boolean> enableModule();

    public void onInitialize() {
        // do nothing
    }

    public void onReload() {
        // do nothing.
    }

}
