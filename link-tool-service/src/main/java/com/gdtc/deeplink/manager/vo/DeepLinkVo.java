package com.gdtc.deeplink.manager.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gdtc.deeplink.manager.utils.StatusEnum;

import java.util.Date;
import java.util.Map;

public class DeepLinkVo extends UTMVo {

    private Integer id;

    private Integer landingPageId;

    private Integer shortLinkId;

    private Map<String, String> paramMap;

    private String landingPagePath;

    private LandingPageVo landingPageVo;

    private String name;

    private String link;

    private String platform;

    private Date expiredTime;

    private String status;

    private Date createTime;

    private Date updateTime;

    private String creator;

    private String editor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShortLinkId() {
        return shortLinkId;
    }

    public void setShortLinkId(Integer shortLinkId) {
        this.shortLinkId = shortLinkId;
    }

    public Integer getLandingPageId() {
        return landingPageId;
    }

    public void setLandingPageId(Integer landingPageId) {
        this.landingPageId = landingPageId;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public String getLandingPagePath() {
        return landingPagePath;
    }

    public void setLandingPagePath(String landingPagePath) {
        this.landingPagePath = landingPagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public LandingPageVo getLandingPageVo() {
        return landingPageVo;
    }

    public void setLandingPageVo(LandingPageVo landingPageVo) {
        this.landingPageVo = landingPageVo;
    }

    /**
     * check whether the deeplink is expired.
     * @return
     */
    @JsonIgnore
    public boolean isExpired() {
        return (null != expiredTime && new Date().after(this.expiredTime)) || StatusEnum.OFF.getCode().equals(this.status);
    }
}
