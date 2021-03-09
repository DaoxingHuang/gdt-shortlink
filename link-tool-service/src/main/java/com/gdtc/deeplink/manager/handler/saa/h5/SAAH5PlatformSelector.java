package com.gdtc.deeplink.manager.handler.saa.h5;

import com.gdtc.deeplink.manager.handler.inf.AbstractPlatformSelector;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.PlatformEnum;

public class SAAH5PlatformSelector implements AbstractPlatformSelector {
    @Override
    public boolean isExpected(LandingPage landingPage) {
        return PlatformEnum.SAA.name().equals(landingPage.getPlatform()) && !landingPage.isNative();
    }
}
