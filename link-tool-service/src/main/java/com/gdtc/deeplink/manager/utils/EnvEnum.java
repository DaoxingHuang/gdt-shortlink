package com.gdtc.deeplink.manager.utils;

public enum EnvEnum {
    DEV("dev"),
    TEST("test"),
    PRE("pre"),
    PROD("prod");

    private String name;

    EnvEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
