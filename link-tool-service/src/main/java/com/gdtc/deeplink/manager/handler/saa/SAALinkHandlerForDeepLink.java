package com.gdtc.deeplink.manager.handler.saa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gdtc.deeplink.manager.handler.inf.AbstractLinkHandler;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public interface SAALinkHandlerForDeepLink extends AbstractLinkHandler<DeepLink> {
    public static final Logger logger = LoggerFactory.getLogger(SAALinkHandlerForDeepLink.class);

    String getSAATemplate();

    @Override
    default String generateLink(DeepLink deepLink, LandingPage landingPage, Map<String, String> paramMap) {
        String path = this.generateLandingPagePath(paramMap, landingPage);
        deepLink.setLandingPagePath(path);

        String link = this.formatLink(path, deepLink.buildUtm(), landingPage.getSchemeName());
        deepLink.setLink(link);
        logger.info(link);
        return link;
    }

    @Override
    default String updateUTM(DeepLink deepLink, LandingPage landingPage) {
        String link = this.formatLink(deepLink.getLandingPagePath(), deepLink.buildUtm(), landingPage.getSchemeName());
        deepLink.setLink(link);
        logger.info(link);
        return link;
    }

    default String formatLink(String landingPagePath, String utm, String schemeName) {
        Map<String, Object> contentMap = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(landingPagePath)) {
            contentMap.putAll(JSONObject.parseObject(landingPagePath, LinkedHashMap.class));
        }
        contentMap.put("utm", utm);

        String content = URLEncoder.encode(URLEncoder.encode(JSON.toJSONString(contentMap)));

        String link = this.getSAATemplate().replace("{scheme}", schemeName).replace("{content}", content);
        return link;
    }
}
