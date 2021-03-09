package com.gdtc.deeplink.manager.filter;

import com.gdtc.deeplink.manager.filter.AuthFilter;

public class ThreadUserInfo {
    private static ThreadLocal<SSOUserInfo> userInfoThreadLocal = new ThreadLocal<>();

    public static void setUserInfo(SSOUserInfo userInfo) {
        userInfoThreadLocal.set(userInfo);
    }

    public static SSOUserInfo getUserInfo() {
        return userInfoThreadLocal.get();
    }

    public static void clear() {
        userInfoThreadLocal.remove();
    }

}
