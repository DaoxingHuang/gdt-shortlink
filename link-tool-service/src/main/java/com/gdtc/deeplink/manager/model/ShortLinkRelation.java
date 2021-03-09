package com.gdtc.deeplink.manager.model;

import javax.persistence.*;

@Table(name = "short_link_relation")
public class ShortLinkRelation {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "short_link_id")
    private Integer shortLinkId;

    @Column(name = "link_type")
    private String linkType;

    @Column(name = "original_id")
    private Integer originalId;

    @Column(name = "original_link")
    private String originalLink;

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

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public Integer getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Integer originalId) {
        this.originalId = originalId;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }
}
