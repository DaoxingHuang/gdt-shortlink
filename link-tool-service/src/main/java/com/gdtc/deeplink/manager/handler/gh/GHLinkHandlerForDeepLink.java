package com.gdtc.deeplink.manager.handler.gh;

import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.inf.AbstractLinkHandler;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.PagePathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Map;

@Service
public class GHLinkHandlerForDeepLink extends GHPlatformSelector implements AbstractLinkHandler<DeepLink> {
    public static final Logger logger = LoggerFactory.getLogger(GHLinkHandlerForDeepLink.class);

    @Autowired
    private ConfigParam configParam;

    @Override
    public String generateLandingPagePath(Map<String, String> paramMap, LandingPage landingPage) {
        String landingPagePath = landingPage.generatePath(paramMap);
        if (StringUtils.isBlank(landingPagePath)) {
            throw new ServiceException("current template need param.");
        }

        if (landingPagePath.length() > Constants.MAX_LANDING_PAGE_LENGTH) {
            throw new ServiceException("landingPage path too long.");
        }
        return PagePathUtils.formatPagePath(landingPagePath);
    }

    @Override
    public String generateLink(DeepLink deepLink, LandingPage landingPage, Map<String, String> paramMap) {
        String landingPagePath = this.generateLandingPagePath(paramMap, landingPage);
        deepLink.setLandingPagePath(landingPagePath);

        String link = this.formatLink(landingPagePath, deepLink.buildUtm());
        deepLink.setLink(link);
        logger.info(link);
        return link;
    }

    @Override
    public String updateUTM(DeepLink deepLink, LandingPage landingPage) {
        String link = this.formatLink(deepLink.getLandingPagePath(), deepLink.buildUtm());
        deepLink.setLink(link);
        logger.info(link);
        return link;
    }

    private String formatLink(String landingPagePath, String utm) {
        String url = this.configParam.getGooddoctorDomain() + landingPagePath;
        url += url.contains("?") ? "&" : "?";
        url += Constants.utmUrlParamKey;
        url += "=";
        url += utm;

        String fullUrl = URLEncoder.encode(URLEncoder.encode(URLEncoder.encode(url)));
        String currentRedirectUrl = URLEncoder.encode(URLEncoder.encode(this.configParam.getRedirectUrl()));
        String webUrl = URLEncoder.encode(this.configParam.getGrabWebUrl());

        String link = this.configParam.getGhTemplate().replace("{webUrl}", webUrl).replace("{redirectUrl}", currentRedirectUrl).replace("{fullPath}", fullUrl);
        return link;
    }
}
