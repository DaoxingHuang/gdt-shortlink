package com.gdtc.link.api.vo;

public class ShortLinkResultVo {
    /**
     * 短链类型
     */
    private String linkType;
    /**
     * 短链
     */
    private String link;
    /**
     * 原始链接（长链）
     */
    private String originalLink;
    /**
     * 短链对应的code
     */
    private String code;
    /**
     * 过期时间
     */
    private long expiredTime;
    /**
     * 创建者
     */
    private String creator;

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

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "ShortLinkResultVo{" +
                "linkType='" + linkType + '\'' +
                ", link='" + link + '\'' +
                ", originalLink='" + originalLink + '\'' +
                ", code='" + code + '\'' +
                ", expiredTime=" + expiredTime +
                ", creator='" + creator + '\'' +
                '}';
    }
}