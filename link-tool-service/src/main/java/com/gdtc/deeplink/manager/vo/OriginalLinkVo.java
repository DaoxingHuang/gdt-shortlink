package com.gdtc.deeplink.manager.vo;

import java.util.Objects;

public class OriginalLinkVo {
    private Integer id;
    private String originalLink;
    private Integer originalId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public Integer getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Integer originalId) {
        this.originalId = originalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OriginalLinkVo)) {
            return false;
        }
        OriginalLinkVo that = (OriginalLinkVo) o;
        return Objects.equals(originalLink, that.originalLink) &&
                Objects.equals(originalId, that.originalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalLink, originalId);
    }
}
