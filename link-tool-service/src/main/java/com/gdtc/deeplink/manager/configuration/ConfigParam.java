package com.gdtc.deeplink.manager.configuration;

import com.alibaba.fastjson.JSON;
import com.gdtc.deeplink.manager.model.SAAScheme;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
public class ConfigParam {
    /**
     * 单个utm可对应的deeplink数目（通常一对一，少数情况下可对应多个deeplink）
     */
    @Value("${deeplink.utm.same-times}")
    private int utmSameTimes;

    @Value("${deeplink.gh.gooddoctor.domain}")
    private String gooddoctorDomain;
    @Value("${deeplink.gh.gooddoctor.redirectUrl}")
    private String redirectUrl;
    @Value("${deeplink.gh.grab.oauthUrl}")
    private String grabWebUrl;
    @Value("${deeplink.gh.template}")
    private String ghTemplate;
    @Value("${deeplink.saa.template}")
    private String saaTemplateForDeepLink;

    @Value("${gdtOnelink.saa.template}")
    private String saaTemplateForGDTOneLink;

    @Value("${shortlink.deeplink.max-count}")
    private int shortToDeepLinkMaxCount;
    @Value("${shortlink.domain}")
    private String shortLinkDomain;

    @Value("${landingpage.modules}")
    private String modules;
    @Value("${landingpage.saa.scheme}")
    private String saaScheme;

    @Value("${facebook.utm.source}")
    private String facebookSourceStr;
    @Value("${facebook.default.onelink.pid}")
    private String facebookDefaultOneLinkPid;

    private Map<String, SAAScheme> saaSchemeMap = new HashMap<>();
    private List<SAAScheme> saaSchemeList = new ArrayList<>();
    private List<String> facebookSourceList = new ArrayList<>();

    @PostConstruct
    private void init() {
        this.saaSchemeList = JSON.parseArray(this.saaScheme, SAAScheme.class);
        this.saaSchemeList.forEach(scheme -> saaSchemeMap.put(scheme.getScheme(), scheme));

        if (StringUtils.isNotBlank(this.facebookSourceStr)) {
            this.facebookSourceList = Arrays.asList(this.facebookSourceStr.split(","));
        }
    }

    public int getUtmSameTimes() {
        return utmSameTimes;
    }

    public String getGooddoctorDomain() {
        return gooddoctorDomain;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getGrabWebUrl() {
        return grabWebUrl;
    }

    public String getGhTemplate() {
        return ghTemplate;
    }

    public String getSAATemplateForDeepLink() {
        return saaTemplateForDeepLink;
    }

    public String getSAATemplateForGDTOneLink() {
        return saaTemplateForGDTOneLink;
    }

    public int getShortToOriginalLinkMaxCount() {
        return shortToDeepLinkMaxCount;
    }

    public List<String> getModuleList() {
        return Arrays.asList(modules.split(","));
    }

//    public Map<String, SAAScheme> getSAASchemeMap() {
//        return this.saaSchemeMap;
//    }
    public List<SAAScheme> getSAASchemeList() {
        return this.saaSchemeList;
    }

    public boolean isNativeScheme(String schemeName) {
        if (StringUtils.isBlank(schemeName) || !this.saaSchemeMap.containsKey(schemeName)) {
            return false;
        }
        return this.saaSchemeMap.get(schemeName).isNative();
    }

    public List<String> getFacebookSourceList() {
        return facebookSourceList;
    }

    public String getFacebookDefaultOneLinkPid() {
        return facebookDefaultOneLinkPid;
    }

    public String getShortLinkDomain() {
        return shortLinkDomain;
    }
}
