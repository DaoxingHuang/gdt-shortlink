package com.gdtc.deeplink.manager.utils;

public class PagePathUtils {

    public static String formatPagePath(String pagePath) {
        if (!pagePath.startsWith("/")) {
            pagePath = "/" + pagePath;
        }
        return pagePath;
    }

    public static String formatPagePathWithDomain(String pagePath, String domain) {
        return new StringBuilder(domain).append(PagePathUtils.formatPagePath(pagePath)).toString();
    }
}
