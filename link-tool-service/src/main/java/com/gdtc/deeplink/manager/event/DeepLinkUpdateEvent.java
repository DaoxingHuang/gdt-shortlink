package com.gdtc.deeplink.manager.event;

import com.gdtc.deeplink.manager.utils.Constants;
import com.gdtc.deeplink.manager.utils.ShortLinkInfoEnum;

public class DeepLinkUpdateEvent extends OriginalLinkUpdateEvent {
    public DeepLinkUpdateEvent(String link, Integer id) {
        super(link);
        this.id = id;
        this.link = link;
        this.type = ShortLinkInfoEnum.DEEPLINK.getLinkType();
    }
}
