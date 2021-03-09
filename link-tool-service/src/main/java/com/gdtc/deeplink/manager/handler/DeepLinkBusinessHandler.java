package com.gdtc.deeplink.manager.handler;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.gh.GHLinkHandlerForDeepLink;
import com.gdtc.deeplink.manager.handler.inf.AbstractLinkHandler;
import com.gdtc.deeplink.manager.handler.saa.h5.SAAH5LinkHandlerForDeepLink;
import com.gdtc.deeplink.manager.handler.saa.app.SAANativeLinkHandlerForDeepLink;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeepLinkBusinessHandler {
    @Autowired
    private GHLinkHandlerForDeepLink ghHandlerForDeepLink;

    @Autowired
    private SAAH5LinkHandlerForDeepLink saah5HandlerForDeepLink;

    @Autowired
    private SAANativeLinkHandlerForDeepLink saaNativeHandlerForDeepLink;

    private List<AbstractLinkHandler> handlerList;

    @PostConstruct
    public void init() {
        handlerList = new ArrayList<>();

        handlerList.add(ghHandlerForDeepLink);
        handlerList.add(saah5HandlerForDeepLink);
        handlerList.add(saaNativeHandlerForDeepLink);
    }

    public String generateLink(DeepLink deepLink, LandingPage landingPage, Map<String, String> paramMap) {
        for (AbstractLinkHandler handler : this.handlerList) {
            if (handler.isExpected(landingPage)) {
                return handler.generateLink(deepLink, landingPage, paramMap);
            }
        }

        throw new ServiceException("unsupported type");
    }

    public String updateUTM(DeepLink deepLink, LandingPage landingPage) {
        for (AbstractLinkHandler handler : this.handlerList) {
            if (handler.isExpected(landingPage)) {
                return handler.updateUTM(deepLink, landingPage);
            }
        }

        throw new ServiceException("unsupported type");
    }
}
