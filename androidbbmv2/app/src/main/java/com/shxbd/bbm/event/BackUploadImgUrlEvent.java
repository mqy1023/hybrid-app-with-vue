package com.shxbd.bbm.event;

// 上传成功后返回h5端图片的链接
public class BackUploadImgUrlEvent {

    public String imgUrl;

    public BackUploadImgUrlEvent(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
