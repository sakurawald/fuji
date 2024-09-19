package io.github.sakurawald.core.config.handler.impl;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;


public class ObjectConfigurationHandler<T> extends BaseConfigurationHandler<T> {

    final Class<T> typeOfModel;

    public ObjectConfigurationHandler(Path path, Class<T> typeOfModel) {
        super(path);
        this.typeOfModel = typeOfModel;

        // set flags
        super.detectUnknownKeysFlag = true;
    }

    public ObjectConfigurationHandler(@NotNull String other, Class<T> typeOfModel) {
        this(ReflectionUtil.getModuleConfigPath(typeOfModel).resolve(other), typeOfModel);
    }

    @SneakyThrows
    @Override
    protected T getDefaultModel() {
        return typeOfModel.getDeclaredConstructor().newInstance();
    }

}
