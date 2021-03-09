package com.gdtc.deeplink.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gdtc.deeplink.manager.utils.Constants;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

@Table(name = "GDT_ONE_LINK")
public class GDTOneLink extends AbstractLink {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LINK")
    private String link;

    /**
     * OneLink自定义参数
     */
    @Column(name = "PARAM")
    private String param;

    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    @Column(name = "utm_content")
    private String utmContent;

    /**
     * 失效时间
     */
    @Column(name = "EXPIRED_TIME")
    private Date expiredTime;

    /**
     * 状态：active\\off
     */
    @Column(name = "STATUS")
    private String status;
    /**
     * 对于的landing_page id
     */
    @Column(name = "LANDING_PAGE_ID")
    private Integer landingPageId;

    /**
     * 对应的landing_page路径
     */
    @Column(name = "LANDING_PAGE_PATH")
    private String landingPagePath;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 创建者
     */
    @Column(name = "CREATOR")
    private String creator;

    /**
     * 修改者
     */
    @Column(name = "EDITOR")
    private String editor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getLandingPageId() {
        return landingPageId;
    }

    public void setLandingPageId(Integer landingPageId) {
        this.landingPageId = landingPageId;
    }

    public String getLandingPagePath() {
        return landingPagePath;
    }

    public void setLandingPagePath(String landingPagePath) {
        this.landingPagePath = landingPagePath;
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

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent;
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

    @JsonIgnore
    public String buildUtm() {
        return String.join(Constants.UTM_CONNECT_FLAG, Arrays.asList(this.utmSource, this.utmMedium, this.utmCampaign, this.utmContent));
    }
}
