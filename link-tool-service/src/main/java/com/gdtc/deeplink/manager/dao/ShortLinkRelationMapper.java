package com.gdtc.deeplink.manager.dao;

import com.gdtc.deeplink.manager.core.Mapper;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;

import java.util.List;

public interface ShortLinkRelationMapper extends Mapper<ShortLinkRelation> {
    void deleteByShortLinkId(Integer shortLinkId);

    void deleteByShortLinkAndOriginalId(Integer shortLinkId, Integer originalId);

    List<ShortLinkRelation> selectByShortLinkId(Integer shortLinkId);
    int countByShortLinkId(Integer shortLinkId);

    ShortLinkRelation selectByLinkTypeAndOriginalId(String linkType, Integer originalId);
    List<ShortLinkRelation> selectByLinkType(String linkType);

}