package com.gdtc.deeplink.manager.handler;

import com.gdtc.deeplink.manager.handler.gh.GHLandingPageValidator;
import com.gdtc.deeplink.manager.handler.inf.AbstractLandingPageValidator;
import com.gdtc.deeplink.manager.handler.saa.app.SAANativeLandingPageValidator;
import com.gdtc.deeplink.manager.handler.saa.h5.SAAH5LandingPageValidator;
import com.gdtc.deeplink.manager.model.LandingPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class LandingPageBusinessHandler {
    @Autowired
    private GHLandingPageValidator ghLandingPageValidator;

    @Autowired
    private SAAH5LandingPageValidator saah5LandingPageValidator;

    @Autowired
    private SAANativeLandingPageValidator saaNativeLandingPageValidator;

    private List<AbstractLandingPageValidator> validatorList;

    @PostConstruct
    public void init() {
        this.validatorList = new ArrayList<>();

        this.validatorList.add(this.ghLandingPageValidator);
        this.validatorList.add(this.saah5LandingPageValidator);
        this.validatorList.add(this.saaNativeLandingPageValidator);
    }

    public void validateLandingPage(LandingPage landingPage) {
        for (AbstractLandingPageValidator handler : this.validatorList) {
            if (handler.isExpected(landingPage)) {
                handler.validateLandingPage(landingPage);
                return;
            }
        }

        throw new RuntimeException("unsupported type");
    }
}
