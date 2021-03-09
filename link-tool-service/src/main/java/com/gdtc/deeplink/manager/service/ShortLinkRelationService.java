package com.gdtc.deeplink.manager.service;

import com.gdtc.deeplink.manager.core.Service;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by GDTC on 2020/11/30.
 */
public interface ShortLinkRelationService extends Service<ShortLinkRelation> {

    ShortLinkRelation findByLinkTypeAndOriginalId(String linkType, Integer originalId);
}
