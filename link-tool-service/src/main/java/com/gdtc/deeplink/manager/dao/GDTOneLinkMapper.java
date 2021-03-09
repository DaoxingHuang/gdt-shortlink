package com.gdtc.deeplink.manager.dao;

import com.gdtc.deeplink.manager.core.Mapper;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.GDTOneLink;

import java.util.List;

public interface GDTOneLinkMapper extends Mapper<GDTOneLink> {
    int countByLandingPage(Integer landingPageId);

    List<GDTOneLink> selectByLandingPage(Integer landingPageId);

    int countByUtm(String source, String medium, String campaign, String content);

    List<GDTOneLink> selectWithoutRelation();
}