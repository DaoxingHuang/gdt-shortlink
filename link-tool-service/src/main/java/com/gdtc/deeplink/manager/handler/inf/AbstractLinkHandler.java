package com.gdtc.deeplink.manager.handler.inf;

import com.gdtc.deeplink.manager.model.AbstractLink;
import com.gdtc.deeplink.manager.model.LandingPage;

import java.util.Map;

public interface AbstractLinkHandler<T extends AbstractLink> extends AbstractPlatformSelector {

    String generateLandingPagePath(Map<String, String> paramMap, LandingPage landingPage);

    String generateLink(T deepLink, LandingPage landingPage, Map<String, String> paramMap);

    String updateUTM(T deepLink, LandingPage landingPage);

}
