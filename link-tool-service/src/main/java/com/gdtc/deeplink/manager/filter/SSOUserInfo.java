package com.gdtc.deeplink.manager.filter;

import io.swagger.annotations.ApiModel;

@ApiModel
public class SSOUserInfo {
    private String username;
    private String chineseName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }
}
