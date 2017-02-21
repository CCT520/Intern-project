package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceSubListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by xiangjun on 15/12/30.
 */
public class DeployPagesRequest {
    //用于handler发送message
    private static final int EXISTED = 0;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;

    //用于返回
    private final static int EDITDEVICEREPLYPAGE_RESULT_CODE = 12;
    private final static int BOTHEDIT_RESULT_CODE = 13;
    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=page_to_device";
    private String token;
    private String device_id;
    private String[] page_ids;
    private String uuid;
    private String major;
    private String minor;
    private String comment;//在同时编辑设备名字和增加回复页面时用
    private EditDeviceFrame thisEditDeviceFrame;
    private int deviceIndexInList;
    private int framesNumberOnThisDevice;
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();
    private String result;

    private boolean bothEdit;
    private android.os.Handler handler;

    private ArrayList<String> newPageIdsArrayList = new ArrayList<String>();


    //以下是部署页面到多个设备用到的变量
    private DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter;
    private RefreshableListView deployBatchOfDeviceSubList;
    private DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame;

    public int getFramesNumberOnThisDevice() {
        return framesNumberOnThisDevice;
    }

    public int getDeviceIndexInList() {
        return deviceIndexInList;
    }

    public EditDeviceFrame getThisEditDeviceFrame() {
        return thisEditDeviceFrame;
    }

    public DeployBatchOfDeviceFrame getThisDeployBatchOfDeviceFrame() {
        return thisDeployBatchOfDeviceFrame;
    }

    public boolean isBothEdit() {
        return bothEdit;
    }

    public String getComment() {
        return comment;
    }

    public Handler getHandler() {
        return handler;
    }


    //这是添加回复页面用到的构造函数
    public DeployPagesRequest(String token, RequestData rd, final EditDeviceFrame
            thisEditDeviceFrame, int deviceIndexInList, int framesNumberOnThisDevice, final boolean bothEdit) {
        this.token = token;
        this.page_ids = rd.getPage_ids();
        this.device_id = rd.getDevice_id();
        this.uuid = rd.getUuid();
        this.major = rd.getMajor();
        this.minor = rd.getMinor();
        this.comment = rd.getComment();
        this.thisEditDeviceFrame = thisEditDeviceFrame;
        this.deviceIndexInList = deviceIndexInList;
        this.framesNumberOnThisDevice = framesNumberOnThisDevice;//用于返回
        this.bothEdit = bothEdit;
        this.url = url + "&token=" + this.token;
        params.put("device_id", this.device_id);
        params.put("uuid", this.uuid);
        params.put("major", this.major);
        params.put("minor", this.minor);

        String page_ids_concat = page_ids[0];
        if (!page_ids[0].equals(""))
            newPageIdsArrayList.add(page_ids[0]);
        for (int i = 1; i < page_ids.length; i++) {
            if (!page_ids[i].equals("")) {
                if (!page_ids[i - 1].equals(""))
                    page_ids_concat += "-";
                page_ids_concat += page_ids[i];
                newPageIdsArrayList.add(page_ids[i]);
            }

        }

        params.put("page_ids", page_ids_concat);

        client.setConnectTimeout(5000);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                if (arg0 == 200) {
                    result = new String(arg2);
                    //解析result
                    //创建一个JSON对象
                    try {
                        JSONObject responseObjec = new JSONObject(result.toString());
                        long err_code = responseObjec.getLong("err_code");
                        if (err_code == 0) {

                            String[] newPageIdsArray = new String[newPageIdsArrayList.size()];
                            for (int i = 0; i < newPageIdsArrayList.size(); i++)
                                newPageIdsArray[i] = newPageIdsArrayList.get(i);

                            if (isBothEdit() == false) {
                                Intent intent = new Intent();
                                intent.putExtra("framesNumberOnThisDevice", getFramesNumberOnThisDevice());
                                intent.putExtra("deviceIndexInList", getDeviceIndexInList());

                                intent.putExtra("relationalPagesIdArray", newPageIdsArray);
                                getThisEditDeviceFrame().setResult(EDITDEVICEREPLYPAGE_RESULT_CODE, intent);
                                getThisEditDeviceFrame().finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("newDeviceName", getComment());
                                intent.putExtra("framesNumberOnThisDevice", getFramesNumberOnThisDevice());
                                intent.putExtra("deviceIndexInList", getDeviceIndexInList());
                                intent.putExtra("relationalPagesIdArray", newPageIdsArray);
                                getThisEditDeviceFrame().setResult(BOTHEDIT_RESULT_CODE, intent);
                                getThisEditDeviceFrame().finish();
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
                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisEditDeviceFrame());
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

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisEditDeviceFrame());
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

    //一个页面部署到多个设备的构造函数
    public DeployPagesRequest(String token, RequestData rd, DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                              RefreshableListView deployBatchOfDeviceSubList, DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,
                              android.os.Handler handler, int deviceIndexInList) {
        this.token = token;
        this.page_ids = rd.getPage_ids();
        this.device_id = rd.getDevice_id();
        this.uuid = rd.getUuid();
        this.major = rd.getMajor();
        this.minor = rd.getMinor();
        this.comment = rd.getComment();
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.thisDeployBatchOfDeviceFrame = thisDeployBatchOfDeviceFrame;
        this.handler = handler;
        this.deviceIndexInList = deviceIndexInList;
        this.url = url + "&token=" + this.token;
        params.put("device_id", this.device_id);
        params.put("uuid", this.uuid);
        params.put("major", this.major);
        params.put("minor", this.minor);
        String page_ids_concat = "";
        for (int i = 0; i < page_ids.length; i++) {
            page_ids_concat += page_ids[i];
            if (i != page_ids.length - 1)
                page_ids_concat += "-";
        }

        params.put("page_ids", page_ids_concat);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                //如果部署不成功
                Message msg = getHandler().obtainMessage();
                msg.what = FAILED;
                msg.arg1 = getDeviceIndexInList();
                getHandler().sendMessage(msg);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                if (arg0 == 200) {
                    result = new String(arg2);
                    //解析result
                    //创建一个JSON对象
                    try {
                        JSONObject responseObjec = new JSONObject(result.toString());
                        long err_code = responseObjec.getLong("err_code");
                        if (err_code == 0) {

                            //部署成功
                            Message msg = getHandler().obtainMessage();
                            msg.what = SUCCESS;
                            msg.arg1 = getDeviceIndexInList();
                            msg.arg2 = page_ids.length;
                            getHandler().sendMessage(msg);

                        } else {
                            //如果部署不成功
                            Message msg = getHandler().obtainMessage();
                            msg.what = FAILED;
                            msg.arg1 = getDeviceIndexInList();
                            getHandler().sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
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



