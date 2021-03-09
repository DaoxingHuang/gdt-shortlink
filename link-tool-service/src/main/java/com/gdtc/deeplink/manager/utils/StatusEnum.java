package com.gdtc.deeplink.manager.utils;

public enum StatusEnum {
    ACTIVE("ACTIVE"),
    OFF("OFF");

    private String code;

    public String getCode() {
        return code;
    }

    StatusEnum(String code) {
        this.code = code;
    }

    public static StatusEnum codeOf(String code) {
        for (StatusEnum tempEnum : StatusEnum.values()) {
            if (tempEnum.code.equals(code)) {
                return tempEnum;
            }
        }
        return null;
    }
}
