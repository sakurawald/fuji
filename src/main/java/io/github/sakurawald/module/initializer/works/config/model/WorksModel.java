package io.github.sakurawald.module.initializer.works.config.model;

import io.github.sakurawald.module.initializer.works.structure.work.interfaces.Work;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArrayList;

public class WorksModel {

    public @NotNull CopyOnWriteArrayList<Work> works = new CopyOnWriteArrayList<>();

}
