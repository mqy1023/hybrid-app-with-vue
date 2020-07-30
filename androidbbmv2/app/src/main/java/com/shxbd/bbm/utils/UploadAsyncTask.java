package com.shxbd.bbm.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.shxbd.bbm.event.BackUploadImgUrlEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadAsyncTask extends AsyncTask<String,Void,String> {

    public UploadAsyncTask(){
    }
    @Override
    protected String doInBackground(String... strings) {
        return UploadUtil.uploadFile(strings[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.e("upload", "re==onPostExecute=====" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);

            Log.e("upload", "result1=====" + jsonObject.optInt("code"));
            Log.e("upload", "result2=====" + jsonObject.optString("data"));

            String resData = jsonObject.optString("data");
            JSONObject resObject = new JSONObject(resData);
            String imgUrl = resObject.optString("relative_url");
            if (imgUrl != null && !"".equals(imgUrl)) {
                EventBus.getDefault().post(new BackUploadImgUrlEvent(resObject.optString("relative_url")));
            } else {
                EventBus.getDefault().post(new BackUploadImgUrlEvent(""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
