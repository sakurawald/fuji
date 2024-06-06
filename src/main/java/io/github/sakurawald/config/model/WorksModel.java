package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.works.work_type.Work;

import java.util.concurrent.CopyOnWriteArrayList;

public class WorksModel extends AbstractModel {

    public CopyOnWriteArrayList<Work> works = new CopyOnWriteArrayList<>();

}
