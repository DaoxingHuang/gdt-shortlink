package com.gdtc.deeplink.manager.utils;

public enum ShortLinkInfoEnum {
    DEEPLINK("deeplink", "/d"),
    GDT_ONE_LINK("oneLink", "/o"),
    COMMON("common", "/c")
    ;

    private String linkType;
    private String prefix;

    ShortLinkInfoEnum(String linkType, String prefix) {
        this.linkType = linkType;
        this.prefix = prefix;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getPrefix() {
        return prefix;
    }
}
