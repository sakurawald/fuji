package io.github.sakurawald.module.initializer.tab_list.faker.config.model;

public class TabListFakerConfigModel {

    public Ping ping = new Ping();

    public static class Ping {
        public int min_ping = 15;
        public int max_ping = 35;
    }
}
