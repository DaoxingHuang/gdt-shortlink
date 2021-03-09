package com.gdtc.deeplink.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gdtc.deeplink.manager.utils.Constants;

import java.util.Arrays;
import java.util.Date;
import javax.persistence.*;

@Table(name = "DEEP_LINK")
public class DeepLink extends AbstractLink {
    /**
     * id
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 对于的utm参数
     */
//    @Column(name = "UTM")
//    private String utm;

    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "utm_medium")
    private String utmMedium;

    @Column(name = "utm_campaign")
    private String utmCampaign;

    @Column(name = "utm_content")
    private String utmContent;

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

    @Column(name = "NAME")
    private String name;

    /**
     * 完整的deeplink
     */
    @Column(name = "LINK")
    private String link;

    /**
     * 业务平台
     */
    @Column(name = "PLATFORM")
    private String platform;

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

    /**
     * 获取id
     *
     * @return ID - id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
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

    /**
     * 获取对于的landing_page id
     *
     * @return LANDING_PAGE_ID - 对于的landing_page id
     */
    public Integer getLandingPageId() {
        return landingPageId;
    }

    /**
     * 设置对于的landing_page id
     *
     * @param landingPageId 对于的landing_page id
     */
    public void setLandingPageId(Integer landingPageId) {
        this.landingPageId = landingPageId;
    }

    /**
     * 获取对应的landing_page路径
     *
     * @return LANDING_PAGE_PATH - 对应的landing_page路径
     */
    public String getLandingPagePath() {
        return landingPagePath;
    }

    /**
     * 设置对应的landing_page路径
     *
     * @param landingPagePath 对应的landing_page路径
     */
    public void setLandingPagePath(String landingPagePath) {
        this.landingPagePath = landingPagePath;
    }

    /**
     * @return NAME
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取完整的deeplink
     *
     * @return LINK - 完整的deeplink
     */
    public String getLink() {
        return link;
    }

    /**
     * 设置完整的deeplink
     *
     * @param link 完整的deeplink
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * 获取业务平台
     *
     * @return PLATFORM - 业务平台
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置业务平台
     *
     * @param platform 业务平台
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 获取失效时间
     *
     * @return EXPIRED_TIME - 失效时间
     */
    public Date getExpiredTime() {
        return expiredTime;
    }

    /**
     * 设置失效时间
     *
     * @param expiredTime 失效时间
     */
    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    /**
     * 获取状态：active\\off
     *
     * @return STATUS - 状态：active\\off
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态：active\\off
     *
     * @param status 状态：active\\off
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     *
     * @return CREATE_TIME - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return UPDATE_TIME - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取创建者
     *
     * @return CREATOR - 创建者
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 设置创建者
     *
     * @param creator 创建者
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取修改者
     *
     * @return EDITOR - 修改者
     */
    public String getEditor() {
        return editor;
    }

    /**
     * 设置修改者
     *
     * @param editor 修改者
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    @JsonIgnore
    public String buildUtm() {
        return String.join(Constants.UTM_CONNECT_FLAG, Arrays.asList(this.utmSource, this.utmMedium, this.utmCampaign, this.utmContent));
    }
}