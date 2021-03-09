package com.gdtc.deeplink.manager.handler.saa.app;

import com.alibaba.fastjson.JSON;
import com.gdtc.deeplink.manager.configuration.ConfigParam;
import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.saa.SAALinkHandlerForGDTOneLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SAANativeLinkHandlerForGDTOneLink extends SAANativePlatformSelector implements SAALinkHandlerForGDTOneLink {
    public static final Logger logger = LoggerFactory.getLogger(SAANativeLinkHandlerForGDTOneLink.class);

    @Autowired
    private ConfigParam configParam;


    @Override
    public String generateLandingPagePath(Map<String, String> paramMap, LandingPage landingPage) {
        List<String> paramList = landingPage.parseParam();
        if (CollectionUtils.isEmpty(paramList)) {
            return Constants.BLANK_STRING;
        }

        if (null == paramMap) {
            throw new ServiceException("current template need param.");
        }

        Map<String, String> paramValueMap = new LinkedHashMap<>();
        paramList.forEach(param -> {
            if (!paramMap.containsKey(param)) {
                throw new ServiceException("current template need param.");
            }
            paramValueMap.put(param, paramMap.get(param));
        });

        String result = JSON.toJSONString(paramValueMap);
        if (result.length() > Constants.MAX_LANDING_PAGE_LENGTH) {
            throw new ServiceException("param too long.");
        }

        return result;
    }

    @Override
    public String getSAATemplate() {
        return this.configParam.getSAATemplateForGDTOneLink();
    }


    @Override
    public List<String> getFacebookSourceList() {
        return this.configParam.getFacebookSourceList();
    }

    @Override
    public String getDefaultFacebookPid() {
        return this.configParam.getFacebookDefaultOneLinkPid();
    }
}
