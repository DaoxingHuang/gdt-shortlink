package com.gdtc.deeplink.manager.handler.gh;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.inf.AbstractLandingPageValidator;
import com.gdtc.deeplink.manager.model.LandingPage;
import com.gdtc.deeplink.manager.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class GHLandingPageValidator extends GHPlatformSelector implements AbstractLandingPageValidator {

    @Override
    public void validateLandingPage(LandingPage landingPage) {
        // for H5 scheme, pathTemplate is required.
        if (StringUtils.isBlank(landingPage.getPathTemplate())) {
            throw new ServiceException("pathTemplate is required.");
        }

        landingPage.setNative(false);
        landingPage.setSchemeName(Constants.BLANK_STRING);
    }
}
