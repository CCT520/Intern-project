package com.example.xiangjun.qingxinyaoyiyao.function;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PageAddRequest {

    private final static int ADD_PAGE_SUCCESS = 17;
    private final static int INVALID_FILE_SIZE = 20;
    private final static int ADD_PAGE_FAILURE = 22;

    private final static int NETWORK_INNORMAL = 400;
    private final static int JSON_EXECPTION = 401;

    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=page_add";
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

    private Handler listenAddingPageHandler;

    public Handler getListenAddingPageHandler() {
        return listenAddingPageHandler;
    }

    public PageAddRequest(String token, RequestData rd, final Handler listenAddingPageHandler) {
        this.token = token;
        this.title = rd.getTitle();
        this.description = rd.getDescription();
        this.page_url = rd.getPage_url();
        this.comment = rd.getComment();
        this.icon_url = rd.getIcon_url();
        this.url = url + "&token=" + this.token;//+"&begin="+this.begin+"&count="+this.count
        this.listenAddingPageHandler = listenAddingPageHandler;
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
                        JSONObject responseJSONObjec = new JSONObject(result);

                        int err_code = responseJSONObjec.getInt("err_code");
                        if(err_code==0){
                            JSONObject data=responseJSONObjec.getJSONObject("data");
                            int page_id=data.getInt("page_id");
                            Message message = getListenAddingPageHandler().obtainMessage();
                            Bundle bundle=new Bundle();
                            bundle.putLong("page_id",page_id);
                            bundle.putString("icon_url", icon_url);
                            message.what = ADD_PAGE_SUCCESS;
                            message.setData(bundle);
                            getListenAddingPageHandler().sendMessage(message);
                        }else if(err_code==9001009){
                            Message message=getListenAddingPageHandler().obtainMessage();
                            message.what=INVALID_FILE_SIZE;
                            getListenAddingPageHandler().sendMessage(message);
                        }else {
                            Message message=getListenAddingPageHandler().obtainMessage();
                            message.what=ADD_PAGE_FAILURE;
                            getListenAddingPageHandler().sendMessage(message);
                        }


                    } catch (JSONException e) {
                        Message message=getListenAddingPageHandler().obtainMessage();
                        message.what=JSON_EXECPTION;
                        getListenAddingPageHandler().sendMessage(message);
                    }


                }else {
                    Message message=getListenAddingPageHandler().obtainMessage();
                    message.what=NETWORK_INNORMAL;
                    getListenAddingPageHandler().sendMessage(message);
                }

            }
        });

    }


}
