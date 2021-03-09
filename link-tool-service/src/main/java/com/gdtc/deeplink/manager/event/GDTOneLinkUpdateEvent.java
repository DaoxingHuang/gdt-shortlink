package com.gdtc.deeplink.manager.event;

import com.gdtc.deeplink.manager.utils.ShortLinkInfoEnum;

public class GDTOneLinkUpdateEvent extends OriginalLinkUpdateEvent {
    public GDTOneLinkUpdateEvent(String link, Integer id) {
        super(link);
        this.id = id;
        this.link = link;
        this.type = ShortLinkInfoEnum.GDT_ONE_LINK.getLinkType();
    }
}
