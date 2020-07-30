package com.shxbd.bbm.event;

public class JumpBrowserEvent {

    public String jumpUrl;

    public JumpBrowserEvent(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }
}
