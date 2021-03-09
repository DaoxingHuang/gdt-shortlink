package com.gdtc.deeplink.manager.service.impl;

import com.gdtc.deeplink.manager.core.AbstractService;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.dao.ShortLinkRelationMapper;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import com.gdtc.deeplink.manager.service.ShortLinkRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by GDTC on 2020/11/30.
 */
@Service
@Transactional
public class ShortLinkRelationServiceImpl extends AbstractService<ShortLinkRelation> implements ShortLinkRelationService {
    @Resource
    private ShortLinkRelationMapper shortLinkRelationMapper;

    @Override
    public ShortLinkRelation findByLinkTypeAndOriginalId(String linkType, Integer originalId) {
        if (null == originalId || StringUtils.isBlank(linkType)) {
            throw new ServiceException("invalid param.");
        }

        ShortLinkRelation shortLinkRelation = this.shortLinkRelationMapper.selectByLinkTypeAndOriginalId(linkType, originalId);
        if (null == shortLinkRelation) {
            return null;
        }
        return shortLinkRelation;
    }
}
