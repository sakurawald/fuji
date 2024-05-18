package io.github.sakurawald.module.initializer.skin.enums;

public enum SkinVariant {

    CLASSIC("classic"),
    SLIM("slim");

    private final String name;

    SkinVariant(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
