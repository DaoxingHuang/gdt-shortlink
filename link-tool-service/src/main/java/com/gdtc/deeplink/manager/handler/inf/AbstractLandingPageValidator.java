package com.gdtc.deeplink.manager.handler.inf;

import com.gdtc.deeplink.manager.model.LandingPage;

public interface AbstractLandingPageValidator extends AbstractPlatformSelector {

    void validateLandingPage(LandingPage landingPage);
}
