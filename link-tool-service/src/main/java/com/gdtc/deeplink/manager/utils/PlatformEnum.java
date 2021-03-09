package com.gdtc.deeplink.manager.utils;

public enum PlatformEnum {
    GH("GH"),
    SAA("SAA");

    private String code;

    public String getCode() {
        return code;
    }

    PlatformEnum(String code) {
        this.code = code;
    }

    public static PlatformEnum codeOf(String code) {
        for (PlatformEnum tempEnum : PlatformEnum.values()) {
            if (tempEnum.code.equals(code)) {
                return tempEnum;
            }
        }
        return null;
    }
}
