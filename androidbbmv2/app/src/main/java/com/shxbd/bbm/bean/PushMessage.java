package com.shxbd.bbm.bean;

import java.io.Serializable;

/**
 * Created by CangQue on 2018/5/23.
 */

public class PushMessage implements Serializable {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String get_ALIYUN_NOTIFICATION_ID_() {
        return _ALIYUN_NOTIFICATION_ID_;
    }

    public void set_ALIYUN_NOTIFICATION_ID_(String _ALIYUN_NOTIFICATION_ID_) {
        this._ALIYUN_NOTIFICATION_ID_ = _ALIYUN_NOTIFICATION_ID_;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }



    private String content;
    private String type;
    private String _ALIYUN_NOTIFICATION_ID_;
    private String data;
    private String jump_url;
    private String title;
    private String message_id;

}
