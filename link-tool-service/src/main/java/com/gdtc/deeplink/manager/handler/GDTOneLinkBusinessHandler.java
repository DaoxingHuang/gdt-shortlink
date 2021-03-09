package com.gdtc.deeplink.manager.handler;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.gdtc.deeplink.manager.handler.inf.AbstractLinkHandler;
import com.gdtc.deeplink.manager.handler.saa.app.SAANativeLinkHandlerForGDTOneLink;
import com.gdtc.deeplink.manager.handler.saa.h5.SAAH5LinkHandlerForGDTOneLink;
import com.gdtc.deeplink.manager.model.DeepLink;
import com.gdtc.deeplink.manager.model.GDTOneLink;
import com.gdtc.deeplink.manager.model.LandingPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GDTOneLinkBusinessHandler {
    @Autowired
    private SAAH5LinkHandlerForGDTOneLink saaH5HandlerForGDTOneLink;

    @Autowired
    private SAANativeLinkHandlerForGDTOneLink saaNativeHandlerForGDTOneLink;

    private List<AbstractLinkHandler> handlerList;

    @PostConstruct
    public void init() {
        handlerList = new ArrayList<>();

        handlerList.add(saaH5HandlerForGDTOneLink);
        handlerList.add(saaNativeHandlerForGDTOneLink);
    }

    public String generateLink(GDTOneLink gdtOneLink, LandingPage landingPage, Map<String, String> paramMap) {
        for (AbstractLinkHandler handler : this.handlerList) {
            if (handler.isExpected(landingPage)) {
                return handler.generateLink(gdtOneLink, landingPage, paramMap);
            }
        }

        throw new ServiceException("unsupported type");
    }

    public String updateUTM(GDTOneLink gdtOneLink, LandingPage landingPage) {
        for (AbstractLinkHandler handler : this.handlerList) {
            if (handler.isExpected(landingPage)) {
                return handler.updateUTM(gdtOneLink, landingPage);
            }
        }

        throw new ServiceException("unsupported type");
    }
}
