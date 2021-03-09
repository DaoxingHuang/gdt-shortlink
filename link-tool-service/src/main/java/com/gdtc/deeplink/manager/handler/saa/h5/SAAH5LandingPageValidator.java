package com.gdtc.deeplink.manager.handler.saa.h5;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.inf.AbstractLandingPageValidator;
import com.gdtc.deeplink.manager.model.LandingPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SAAH5LandingPageValidator extends SAAH5PlatformSelector implements AbstractLandingPageValidator {

    @Override
    public void validateLandingPage(LandingPage landingPage) {
        if (StringUtils.isBlank(landingPage.getSchemeName())) {
            throw new ServiceException("scheme is required.");
        }

        // for H5 scheme, pathTemplate is required.
        if (StringUtils.isBlank(landingPage.getPathTemplate())) {
            throw new ServiceException("pathTemplate is required.");
        }
    }
}
