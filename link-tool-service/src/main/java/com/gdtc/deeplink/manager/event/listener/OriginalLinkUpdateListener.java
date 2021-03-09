package com.gdtc.deeplink.manager.event.listener;

import com.gdtc.deeplink.manager.event.OriginalLinkUpdateEvent;
import com.gdtc.deeplink.manager.model.ShortLinkRelation;
import com.gdtc.deeplink.manager.service.ShortLinkRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OriginalLinkUpdateListener {
    public static final Logger logger = LoggerFactory.getLogger(OriginalLinkUpdateListener.class);

    @Autowired
    private ShortLinkRelationService shortLinkRelationService;

    @EventListener
    public void onMessage(OriginalLinkUpdateEvent event) {
        ShortLinkRelation relation = shortLinkRelationService.findByLinkTypeAndOriginalId(event.getType(), event.getId());
        if (null == relation) {
            return;
        }

        relation.setOriginalLink(event.getLink());

        this.shortLinkRelationService.update(relation);
        logger.info("finish update ShortLinkRelation, linkType: {}, id:{}", event.getType(), event.getId());
    }
}
