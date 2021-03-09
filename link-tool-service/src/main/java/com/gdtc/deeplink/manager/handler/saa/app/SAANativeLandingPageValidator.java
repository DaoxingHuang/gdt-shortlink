package com.gdtc.deeplink.manager.handler.saa.app;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.inf.AbstractLandingPageValidator;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SAANativeLandingPageValidator extends SAANativePlatformSelector implements AbstractLandingPageValidator {
    @Override
    public void validateLandingPage(LandingPage landingPage) {
        if (StringUtils.isBlank(landingPage.getSchemeName())) {
            throw new ServiceException("scheme is required.");
        }

        // for native scheme, pathTemplate is not required.
        if (StringUtils.isBlank(landingPage.getPathTemplate())) {
            landingPage.setPathTemplate(Constants.BLANK_STRING);
        }
    }
}
