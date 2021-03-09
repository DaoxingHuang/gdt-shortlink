package com.gdtc.deeplink.manager.handler.saa.h5;

import com.alibaba.fastjson.JSON;
import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.saa.SAALinkHandlerForDeepLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.PagePathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SAAH5LinkHandlerForDeepLink extends SAAH5PlatformSelector implements SAALinkHandlerForDeepLink {
    public static final Logger logger = LoggerFactory.getLogger(SAAH5LinkHandlerForDeepLink.class);

    @Autowired
    private ConfigParam configParam;

    @Override
    public String generateLandingPagePath(Map<String, String> paramMap, LandingPage landingPage) {
        String landingPagePath = landingPage.generatePath(paramMap);
        if (StringUtils.isBlank(landingPagePath)) {
            throw new ServiceException("current template need param.");
        }


        Map<String, String> landingPageParamMap = new HashMap<>();
        String url = PagePathUtils.formatPagePathWithDomain(landingPagePath, this.configParam.getGooddoctorDomain());
        landingPageParamMap.put("url", url);

        String result = JSON.toJSONString(landingPageParamMap);
        if (result.length() > Constants.MAX_LANDING_PAGE_LENGTH) {
            throw new ServiceException("landingPage path too long.");
        }
        return result;
    }

    @Override
    public String getSAATemplate() {
        return this.configParam.getSAATemplateForDeepLink();
    }
}
