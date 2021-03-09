package com.gdtc.deeplink.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gdtc.deeplink.manager.utils.StatusEnum;

import java.util.Date;
import javax.persistence.*;

@Table(name = "SHORT_LINK")
public class ShortLink {
    /**
     * Id
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 编码
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 过期时间
     */
    @Column(name = "EXPIRED_TIME")
    private Date expiredTime;

    /**
     * 名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 状态：active/off
     */
    @Column(name = "STATUS")
    private String status;

    @Column(name = "LINK_TYPE")
    private String linkType;

    @Column(name = "link")
    private String link;
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

    @Column(name = "EDITOR")
    private String editor;

    /**
     * 获取Id
     *
     * @return ID - Id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置Id
     *
     * @param id Id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取编码
     *
     * @return CODE - 编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置编码
     *
     * @param code 编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取过期时间
     *
     * @return EXPIRED_TIME - 过期时间
     */
    public Date getExpiredTime() {
        return expiredTime;
    }

    /**
     * 设置过期时间
     *
     * @param expiredTime 过期时间
     */
    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    /**
     * 获取名称
     *
     * @return NAME - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取状态：active/off
     *
     * @return STATUS - 状态：active/off
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态：active/off
     *
     * @param status 状态：active/off
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
     * @return EDITOR
     */
    public String getEditor() {
        return editor;
    }

    /**
     * @param editor
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    @JsonIgnore
    public boolean isValid() {
        return new Date().after(this.expiredTime) || StatusEnum.OFF.getCode().equals(this.status);
    }
}