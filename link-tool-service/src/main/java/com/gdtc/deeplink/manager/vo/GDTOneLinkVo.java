package com.gdtc.deeplink.manager.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class GDTOneLinkVo extends UTMVo {

    private Integer id;

    private Integer shortLinkId;

    private Integer landingPageId;

    private Map<String, String> paramMap;

    private String landingPagePath;

    private LandingPageVo landingPageVo;

    private List<Map<String, String>> customizeParamList;

    private String name;

    private String link;

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

    public List<Map<String, String>> getCustomizeParamList() {
        return customizeParamList;
    }

    public void setCustomizeParamList(List<Map<String, String>> customizeParamList) {
        this.customizeParamList = customizeParamList;
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
}
