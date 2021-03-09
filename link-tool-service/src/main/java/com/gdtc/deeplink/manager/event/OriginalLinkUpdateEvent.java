package com.gdtc.deeplink.manager.event;

import org.springframework.context.ApplicationEvent;

public abstract class OriginalLinkUpdateEvent extends ApplicationEvent {
    protected String link;
    protected Integer id;
    protected String type;

    public OriginalLinkUpdateEvent(Object source) {
        super(source);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
