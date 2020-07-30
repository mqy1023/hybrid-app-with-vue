package com.shxbd.bbm.event;

public class PickerImgUploadEvent {

    public String token;

    public PickerImgUploadEvent(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
