package com.gdtc.deeplink.manager.dao;

import com.gdtc.deeplink.manager.core.Mapper;
import com.gdtc.deeplink.manager.model.ShortLink;

public interface ShortLinkMapper extends Mapper<ShortLink> {
    ShortLink selectByCode(String code);
}