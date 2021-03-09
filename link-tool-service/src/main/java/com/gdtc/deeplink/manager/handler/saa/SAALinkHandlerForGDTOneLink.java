package com.gdtc.deeplink.manager.handler.saa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gdtc.deeplink.manager.handler.inf.AbstractLinkHandler;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;

public interface SAALinkHandlerForGDTOneLink extends AbstractLinkHandler<GDTOneLink> {
    public static final Logger logger = LoggerFactory.getLogger(SAALinkHandlerForGDTOneLink.class);

    String getSAATemplate();

    List<String> getFacebookSourceList();

    String getDefaultFacebookPid();

    @Override
    default String generateLink(GDTOneLink gdtOneLink, LandingPage landingPage, Map<String, String> paramMap) {
        String path = this.generateLandingPagePath(paramMap, landingPage);
        gdtOneLink.setLandingPagePath(path);

        String link = this.formatLink(gdtOneLink, landingPage.getSchemeName());
        gdtOneLink.setLink(link);
        logger.info(link);
        return link;
    }

    @Override
    default String updateUTM(GDTOneLink gdtOneLink, LandingPage landingPage) {
        String link = this.formatLink(gdtOneLink, landingPage.getSchemeName());
        gdtOneLink.setLink(link);
        logger.info(link);
        return link;
    }

    // https://gooddoctor.onelink.me/xxxx/?pid={pid}&c={c}&scheme={scheme}%3Fcontent%3D{content}
    default String formatLink(GDTOneLink gdtOneLink, String schemeName) {
        Map<String, Object> contentMap = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(gdtOneLink.getLandingPagePath())) {
            contentMap.putAll(JSONObject.parseObject(gdtOneLink.getLandingPagePath(), LinkedHashMap.class));
        }
        contentMap.put("utm", gdtOneLink.buildUtm());

        StringBuilder customizeUrlParamBuilder = new StringBuilder();
        this.buildDefaultParam(gdtOneLink).forEach(item -> {
            String key = String.valueOf(item.get("key"));
            String value = String.valueOf(item.get("value"));
            customizeUrlParamBuilder.append("&").append(key).append("=").append(URLEncoder.encode(value));
        });

        if (StringUtils.isNotBlank(gdtOneLink.getParam())) {
            JSON.parseArray(gdtOneLink.getParam(), Map.class).forEach(item -> {
                String key = String.valueOf(item.get("key"));
                String value = String.valueOf(item.get("value"));
                customizeUrlParamBuilder.append("&").append(key).append("=").append(URLEncoder.encode(value));
            });
        }

        String content = URLEncoder.encode(URLEncoder.encode(JSON.toJSONString(contentMap)));
        // oneLink special parameter.
        String pid = this.getPidForGDTOneLink(gdtOneLink.getUtmSource());
        String c = gdtOneLink.getUtmCampaign();

        String link = this.getSAATemplate()
                .replace("{pid}", pid)
                .replace("{c}", c)
                .replace("{scheme}", schemeName)
                .replace("{content}", content);
        link += customizeUrlParamBuilder.toString();
        return link;
    }

    default List<Map<String, String>> buildDefaultParam(GDTOneLink gdtOneLink) {
        String af_adset = gdtOneLink.getUtmCampaign();
        String af_ad = gdtOneLink.getUtmContent();

        List<Map<String, String>> defaultParamMapList = new ArrayList<>();
        {
            Map<String, String> defaultParamMap = new HashMap<>();
            defaultParamMap.put("key", "af_adset");
            defaultParamMap.put("value", af_adset);
            defaultParamMapList.add(defaultParamMap);
        }
        {
            Map<String, String> defaultParamMap = new HashMap<>();
            defaultParamMap.put("key", "af_ad");
            defaultParamMap.put("value", af_ad);
            defaultParamMapList.add(defaultParamMap);
        }
        return defaultParamMapList;
    }

    default String getPidForGDTOneLink(String utmSource) {
        String pid = utmSource;

        boolean isFacebookSource = this.getFacebookSourceList().stream().anyMatch(source -> utmSource.equalsIgnoreCase(source));
        if (isFacebookSource) {
            pid = this.getDefaultFacebookPid();
        }
        return pid;
    }
}
