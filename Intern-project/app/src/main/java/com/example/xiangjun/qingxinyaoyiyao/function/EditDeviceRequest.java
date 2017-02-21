package com.example.xiangjun.qingxinyaoyiyao.function;

import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrame;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EditDeviceRequest {

    private final static int EDITDEVICE_RESULT_CODE = 3;

    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=device_edit";
    TextView show;
    private String token;
    private int deviceIndexInList;
    private String device_id;
    private String[] page_ids;//用于在同时更改设备名称和增加回复页面时
    private String minor;
    private String uuid;
    private String major;
    private String comment;
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();
    private String result = null;
    private EditDeviceFrame thisEditDeviceFrame;
    private boolean bothEdit;
    private int framesNumberOnThisDevice;

    public EditDeviceFrame getThisEditDeviceFrame() {
        return thisEditDeviceFrame;
    }

    public String getComment() {
        return comment;
    }

    public int getDeviceIndexInList() {
        return deviceIndexInList;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getMinor() {
        return minor;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMajor() {
        return major;
    }

    public int getFramesNumberOnThisDevice() {
        return framesNumberOnThisDevice;
    }

    public String getToken() {
        return token;
    }

    public String[] getPage_ids() {
        return page_ids;
    }

    public boolean isBothEdit() {
        return bothEdit;
    }

    public EditDeviceRequest(String token, RequestData rd, final EditDeviceFrame thisEditDeviceFrame, int deviceIndexInList, final boolean bothEdit, int framesNumberOnThisDevice) {
        this.token = token;
        this.deviceIndexInList = deviceIndexInList;
        this.device_id = rd.getDevice_id();
        this.major = rd.getMajor();
        this.uuid = rd.getUuid();
        this.minor = rd.getMinor();
        this.comment = rd.getComment();
        this.page_ids = rd.getPage_ids();
        this.url = url + "&token=" + this.token;
        this.thisEditDeviceFrame = thisEditDeviceFrame;
        this.bothEdit = bothEdit;
        this.framesNumberOnThisDevice = framesNumberOnThisDevice;
        params.put("device_id", this.device_id);
        params.put("major", this.major);
        params.put("uuid", this.uuid);
        params.put("minor", this.minor);
        params.put("comment", this.comment);

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

                    try {
                        result = new String(arg2, "UTF-8");
                        //解析result
                        //创建一个JSON对象
                        JSONObject responseObjec = new JSONObject(result);
                        String errmsg = responseObjec.getString("err_msg");
                        if (errmsg.equals("success")) {
                            if (bothEdit == false) {//如果只单独设置设备名的话则直接返回
                                Intent intent = new Intent();
                                intent.putExtra("newDeviceName", getComment());
                                intent.putExtra("deviceIndexInList", getDeviceIndexInList());
                                getThisEditDeviceFrame().setResult(EDITDEVICE_RESULT_CODE, intent);
                                getThisEditDeviceFrame().finish();
                            } else {
                                RequestData deployPagesOnOneDeviceRequestData = new RequestData(null, null, getDevice_id(), getMajor(), getPage_ids(), null,
                                        getMinor(), getUuid(), getComment(), null, null, null, null);
                                ConnectRequest deployPagesOnOneDeviceConnectrequest = new ConnectRequest(getToken(), getThisEditDeviceFrame(), getDeviceIndexInList(), getFramesNumberOnThisDevice(), isBothEdit());
                                deployPagesOnOneDeviceConnectrequest.RequestAPI(RequestKind.DeployPages, deployPagesOnOneDeviceRequestData);
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getThisEditDeviceFrame());
                            builder.setTitle("提示");
                            builder.setMessage("请求错误");
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }


                    } catch (UnsupportedEncodingException e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getThisEditDeviceFrame());
                        builder.setTitle("提示");
                        builder.setMessage("出现UnsupportedEncoding异常，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    } catch (JSONException e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getThisEditDeviceFrame());
                        builder.setTitle("提示");
                        builder.setMessage("JSON包解析出错，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getThisEditDeviceFrame());
                    builder.setTitle("提示");
                    builder.setMessage("网络异常,请检查网络!");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }
        });
    }

}
