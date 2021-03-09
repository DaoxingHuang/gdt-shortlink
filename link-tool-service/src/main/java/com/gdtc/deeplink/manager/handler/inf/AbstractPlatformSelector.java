package com.gdtc.deeplink.manager.handler.inf;

import com.gdtc.deeplink.manager.model.LandingPage;

public interface AbstractPlatformSelector {
    boolean isExpected(LandingPage landingPage);
}
