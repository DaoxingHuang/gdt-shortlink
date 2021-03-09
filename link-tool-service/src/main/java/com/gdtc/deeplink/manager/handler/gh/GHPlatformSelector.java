package com.gdtc.deeplink.manager.handler.gh;

import com.gdtc.deeplink.manager.handler.inf.AbstractPlatformSelector;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.PlatformEnum;

public class GHPlatformSelector implements AbstractPlatformSelector {
    @Override
    public boolean isExpected(LandingPage landingPage) {
        return PlatformEnum.GH.name().equals(landingPage.getPlatform());
    }
}
