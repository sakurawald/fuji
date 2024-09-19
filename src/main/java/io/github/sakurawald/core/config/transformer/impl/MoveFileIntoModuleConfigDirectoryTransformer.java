package io.github.sakurawald.core.config.transformer.impl;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;


public class MoveFileIntoModuleConfigDirectoryTransformer extends MoveFileTransformer{

    public MoveFileIntoModuleConfigDirectoryTransformer(Class<?> clazz) {
        super(ReflectionUtil.getModuleConfigPath(clazz));
    }
}
