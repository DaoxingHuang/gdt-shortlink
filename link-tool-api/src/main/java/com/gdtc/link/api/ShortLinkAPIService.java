package com.gdtc.link.api;

import com.gdtc.link.api.core.Result;
import com.gdtc.link.api.vo.ShortLinkResultVo;


public interface ShortLinkAPIService {
    /**
     * 创建shortLink，通用场景
     * @param originalLink 原始链接，长链
     * @param expiredTime 短链过期时间
     * @param creator 创建者（可以是调用方应用名）
     * @return
     */
    Result<ShortLinkResultVo> createForCommon(String originalLink, long expiredTime, String creator);
}
