package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.works.work_type.Work;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArrayList;

public class WorksModel {

    public @NotNull CopyOnWriteArrayList<Work> works = new CopyOnWriteArrayList<>();

}
