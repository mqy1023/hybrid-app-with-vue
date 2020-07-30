package com.shxbd.bbm.event;

public class JumpH5Event {

    public String jumpUrl;
    public String params;

    public JumpH5Event(String jumpUrl, String params) {
        this.jumpUrl = jumpUrl;
        this.params = params;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public String getParams() {
        return params;
    }
}
