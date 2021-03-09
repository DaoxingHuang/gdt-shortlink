package com.gdtc.deeplink.manager.dao;

import com.gdtc.deeplink.manager.core.Mapper;
import com.gdtc.deeplink.manager.model.DeepLink;

import java.util.List;

public interface DeepLinkMapper extends Mapper<DeepLink> {
    int countByLandingPage(Integer landingPageId);

    List<DeepLink> selectByLandingPage(Integer landingPageId);

    List<DeepLink> selectByUtm(String source, String medium, String campaign, String content);
    int countByUtm(String source, String medium, String campaign, String content);

    List<DeepLink> selectByPlatform(String platform);

    List<DeepLink> selectWithoutRelation();
}