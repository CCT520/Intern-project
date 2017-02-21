package com.example.xiangjun.qingxinyaoyiyao.function;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PageEditRequest {

    private final static int EDIT_PAGE_SUCCESS = 19;
    private final static int INVALID_FILE_SIZE = 20;
    private final static int EDIT_PAGE_FAILURE = 21;

    private final static int NETWORK_INNORMAL = 400;
    private final static int JSON_EXCEPTION = 401;

    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=page_edit";
    private String token;
    private String[] page_ids;
    private String title;
    private String description;
    private String TAG = "TIAN";
    private String page_url;
    private String comment;
    private String icon_url;
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();
    private String result;
    private Handler listenEditingPageHandler;

    public PageEditRequest(String token, RequestData rd, final Handler listenEditingPageHandler) {
        this.token = token;
        this.listenEditingPageHandler=listenEditingPageHandler;
        this.page_ids = rd.getPage_ids();
        this.title = rd.getTitle();
        this.description = rd.getDescription();
        this.page_url = rd.getPage_url();
        this.comment = rd.getComment();
        this.icon_url = rd.getIcon_url();
        this.url = url + "&token=" + this.token;//+"&begin="+this.begin+"&count="+this.count
        String toEditPageId=this.page_ids[0];
        params.put("page_id", toEditPageId);
        params.put("title", this.title);
        params.put("description", this.description);
        params.put("page_url", this.page_url);
        params.put("comment", this.comment);
        params.put("icon_url", this.icon_url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (arg0 == 200) {

                    result = new String(arg2);

                    try {
                        JSONObject resultJSONObjec=new JSONObject(result);
                        int err_code=resultJSONObjec.getInt("err_code");
                        if(err_code==0){
                            Message message=listenEditingPageHandler.obtainMessage();
                            message.what=EDIT_PAGE_SUCCESS;
                            Bundle bundle=new Bundle();
                            bundle.putString("icon_url",icon_url);
                            message.setData(bundle);
                            listenEditingPageHandler.sendMessage(message);
                        }else if(err_code==9001009){
                            Message message=listenEditingPageHandler.obtainMessage();
                            message.what=INVALID_FILE_SIZE;
                            listenEditingPageHandler.sendMessage(message);
                        }else {
                            Message message=listenEditingPageHandler.obtainMessage();
                            message.what=EDIT_PAGE_FAILURE;
                            listenEditingPageHandler.sendMessage(message);
                        }


                    } catch (JSONException e) {
                        Message message=listenEditingPageHandler.obtainMessage();
                        message.what=JSON_EXCEPTION;
                        listenEditingPageHandler.sendMessage(message);
                    }

                }else {
                    Message message=listenEditingPageHandler.obtainMessage();
                    message.what=NETWORK_INNORMAL;
                    listenEditingPageHandler.sendMessage(message);
                }

            }
        });

    }

    public String getResult() {
        return result;
    }
}


