package com.example.xiangjun.qingxinyaoyiyao.function;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceSubListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.MydeviceListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RelationSearchRequest {

    //用于handler发送message
    private static final int EXISTED = 0;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;

    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=relation_search";
    TextView show;
    private String begin;
    private String count;
    private String token;
    private String device_id;
    private String minor;
    private String uuid;
    private String TAG = "TIAN";
    private String major;
    private String type;
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();
    private String result;
    private RequestData rd;
    private MydeviceListAdapter mydeviceListAdapter;
    private RefreshableListView myDevicelv;
    private int relationIndex;
    private MainFrame thisMainFrame;

    //下面是“部署到设备”页面获取的设备列表用到的变量
    private DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter;
    private RefreshableListView deployBatchOfDeviceSubList;

    private android.os.Handler handler;
    private String frameId;
    private DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame;

    public String getToken() {
        return token;
    }

    public RequestData getRd() {
        return rd;
    }

    public String getBegin() {
        return begin;
    }

    public String getCount() {
        return count;
    }

    public MydeviceListAdapter getMydeviceListAdapter() {
        return mydeviceListAdapter;
    }

    public int getRelationIndex() {
        return relationIndex;
    }

    public MainFrame getThisMainFrame() {
        return thisMainFrame;
    }

    public RefreshableListView getMyDevicelv() {
        return myDevicelv;
    }

    public String getFrameId() {
        return frameId;
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

    public DeployBatchOfDeviceSubListAdapter getDeployBatchOfDeviceSubListAdapter() {
        return deployBatchOfDeviceSubListAdapter;
    }

    public RefreshableListView getDeployBatchOfDeviceSubList() {
        return deployBatchOfDeviceSubList;
    }

    public DeployBatchOfDeviceFrame getThisDeployBatchOfDeviceFrame() {
        return thisDeployBatchOfDeviceFrame;
    }

    public Handler getHandler() {
        return handler;
    }


    //这是在“我的设备”页面上请求的页面个数
    public RelationSearchRequest(String token, RequestData rd, MydeviceListAdapter myDeviceAdapter, RefreshableListView myDevicelv,
                                 int relationIndex, MainFrame thisMainFrame) {
        this.token = token;
        this.rd = rd;
        this.mydeviceListAdapter = myDeviceAdapter;
        this.myDevicelv = myDevicelv;
        this.relationIndex = relationIndex;
        this.thisMainFrame = thisMainFrame;
        this.begin=rd.getBegin();
        this.count=rd.getCount();
        this.device_id = rd.getDevice_id();
        this.major = rd.getMajor();
        this.uuid = rd.getUuid();
        this.minor = rd.getMinor();
        this.type = rd.getType();
        this.url = url + "&token=" + this.token;//+"&begin="+this.begin+"&count="+this.count
        Map<String, String> map = new HashMap<String, String>();
        map.put("device_id", this.device_id);
        map.put("uuid", this.uuid);
        map.put("major", this.major);
        map.put("minor", this.minor);
        params.put("device_identifier", map);
        params.put("type", this.type);
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
                    //解析result
                    //创建一个JSON对象
                    JSONObject responseObjec = null;
                    try {
                        responseObjec = new JSONObject(result);

                        int err_code=responseObjec.getInt("err_code");
                        if(err_code==0){
                            JSONObject data = responseObjec.getJSONObject("data");
                            int pageCount = data.getInt("total_count");

                            int relationIndex = getRelationIndex();
                            //将获取的页面数缓存
                            SharedPreferences mySharedPreferences = getThisMainFrame().getSharedPreferences("pageNumberCache",
                                    Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                            editor.putBoolean("pageNumberIsCached", true);

                            Date date = new Date();
                            editor.putLong("pageNumberCachedTime", date.getTime());

                            if (pageCount == 0) {

                                getMydeviceListAdapter().setItem(relationIndex, "deviceHint", "未绑定页面");
                                editor.putString("pageNumberOfDevice" + relationIndex, "0");
                                getMyDevicelv().setAdapter(getMydeviceListAdapter(), null, null, null,null);
                            } else {
                                JSONArray relations = data.getJSONArray("relations");
                                String[] relationalPagesId = new String[pageCount];
                                for (int i = 0; i < pageCount; i++) {
                                    relationalPagesId[i] = String.valueOf(relations.getJSONObject(i).getInt("page_id"));
                                }

                                editor.putString("pageNumberOfDevice" + relationIndex, "" + pageCount);

                                String relationPagesIdSerialize = relationalPagesId[0];
                                for (int i = 1; i < relationalPagesId.length; i++) {
                                    relationPagesIdSerialize += "@";
                                    relationPagesIdSerialize += relationalPagesId[i];
                                }
                                editor.putString("relationalPagesIdArrayOfDevice" + relationIndex, relationPagesIdSerialize);
                                editor.commit();


                                getMydeviceListAdapter().setItem(relationIndex, "deviceHint", pageCount + "个页面");
                                getMydeviceListAdapter().addItemsAtMap(relationIndex, "relationalPagesIdArray", relationalPagesId);
                                getMyDevicelv().setAdapter(getMydeviceListAdapter(), null, null, null,null);
                                getMyDevicelv().setOnRefreshMyDeviceListCompleteParams(getToken(), getMyDevicelv(), getThisMainFrame());
                                getMydeviceListAdapter().setLastLoadingMaxIndex(Integer.valueOf(getBegin()) + Integer.valueOf(getCount()) - 1);
                                if(getMyDevicelv().getMyDeviceStateBeforeRefreshing()!=null)
                                    getMyDevicelv().onRestoreInstanceState(getMyDevicelv().getMyDeviceStateBeforeRefreshing());
                            }


                        }else{
                            String err_msg = responseObjec.getString("err_msg");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                            builder.setTitle("提示");
                            builder.setMessage("在加载页面个数时发生了未知错误\n" +
                                    "错误代码：" + err_code + "\n" +
                                    "错误信息：" + err_msg + "\n" +
                                    "请下拉重试或登录氢心官网申请支持");
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }

                    } catch (JSONException e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());
                        builder.setTitle("提示");
                        builder.setMessage("在加载页面个数时JSON包解析出错，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }


                }else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());
                    builder.setTitle("提示");
                    builder.setMessage("在加载页面个数时网络异常，请检查！");
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

    //这是在部署页面中获取页面个数的构造函数
    public RelationSearchRequest(String token, RequestData rd, DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                                 RefreshableListView deployBatchOfDeviceSubList, int relationIndex) {
        this.token = token;
        this.thisDeployBatchOfDeviceFrame=thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.relationIndex = relationIndex;
        this.begin=rd.getBegin();
        this.count=rd.getCount();
        this.device_id = rd.getDevice_id();
        this.major = rd.getMajor();
        this.uuid = rd.getUuid();
        this.minor = rd.getMinor();
        this.type = rd.getType();
        this.url = url + "&token=" + this.token;//+"&begin="+this.begin+"&count="+this.count
        Map<String, String> map = new HashMap<String, String>();
        map.put("device_id", this.device_id);
        map.put("uuid", this.uuid);
        map.put("major", this.major);
        map.put("minor", this.minor);
        params.put("device_identifier", map);
        params.put("type", this.type);
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
                    //解析result
                    //创建一个JSON对象
                    JSONObject responseObjec = null;
                    try {
                        responseObjec = new JSONObject(result);
                        JSONObject data = responseObjec.getJSONObject("data");
                        int pageCount = data.getInt("total_count");

                        int relationIndex = getRelationIndex();

                        if(pageCount==0)
                            getDeployBatchOfDeviceSubListAdapter().setItem(relationIndex, "deployBatchOfDeviceSubListItemNumber", "未绑定页面");
                        else
                            getDeployBatchOfDeviceSubListAdapter().setItem(relationIndex, "deployBatchOfDeviceSubListItemNumber", pageCount + "个页面");
                        getDeployBatchOfDeviceSubList().setAdapter(null, null, getDeployBatchOfDeviceSubListAdapter(), null,null);
                        getDeployBatchOfDeviceSubListAdapter().setLastLoadingMaxIndex(Integer.valueOf(getBegin())+Integer.valueOf(getCount())-1);
                        if(getDeployBatchOfDeviceSubList().getDeployBatchOfDeviceSubListStateBeforeRefreshing()!=null)
                            getDeployBatchOfDeviceSubList().onRestoreInstanceState(getDeployBatchOfDeviceSubList().getDeployBatchOfDeviceSubListStateBeforeRefreshing());
                    } catch (JSONException e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
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
                    AlertDialog.Builder builder=new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
                    builder.setTitle("提示");
                    builder.setMessage("网络异常，请检查！");
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

    //这是在searchDevice中部署页面时获取原来的id的构造函数
    public RelationSearchRequest(String token, RequestData rd, DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame, DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                                 RefreshableListView deployBatchOfDeviceSubList, android.os.Handler handler, int relationIndex, String frameId) {
        this.token = token;
        this.device_id = rd.getDevice_id();
        this.major = rd.getMajor();
        this.minor = rd.getMinor();
        this.uuid = rd.getUuid();
        this.thisDeployBatchOfDeviceFrame=thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.handler = handler;
        this.relationIndex = relationIndex;
        this.frameId = frameId;
        this.type = rd.getType();
        this.url = url + "&token=" + this.token;//+"&begin="+this.begin+"&count="+this.count
        Map<String, String> map = new HashMap<String, String>();
        map.put("device_id", this.device_id);
        map.put("uuid", this.uuid);
        map.put("major", this.major);
        map.put("minor", this.minor);
        params.put("device_identifier", map);
        params.put("type", this.type);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                Message msg = getHandler().obtainMessage();
                msg.what = FAILED;
                msg.arg1 = getRelationIndex();
                getHandler().sendMessage(msg);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (arg0 == 200) {
                    result = new String(arg2);
                    //解析result
                    //创建一个JSON对象
                    JSONObject responseObjec = null;
                    try {
                        responseObjec = new JSONObject(result);
                        JSONObject data = responseObjec.getJSONObject("data");
                        int pageCount = data.getInt("total_count");
                        if (pageCount == 0) {//如果原来是0，那就根本没有东西，直接加就好
                            String[] page_ids = {getFrameId()};
                            RequestData deployBatchOfDevicesRequestData = new RequestData(null, null, getDevice_id(), getMajor(), page_ids, null, getMinor(), getUuid(), null, null, null, null, null);

                            ConnectRequest deployBatchOfDevicesConnectrequest = new ConnectRequest(getToken(), getDeployBatchOfDeviceSubListAdapter(), getDeployBatchOfDeviceSubList(),
                                    getThisDeployBatchOfDeviceFrame(), getHandler(), getRelationIndex());

                            deployBatchOfDevicesConnectrequest.RequestAPI(RequestKind.DeployPages, deployBatchOfDevicesRequestData);

                        } else {
                            JSONArray relations = data.getJSONArray("relations");
                            String[] relationalPagesId = new String[pageCount];
                            for (int i = 0; i < pageCount; i++) {
                                relationalPagesId[i] = String.valueOf(relations.getJSONObject(i).getInt("page_id"));
                            }

                            boolean hasExited = false;
                            for (int i = 0; i < relationalPagesId.length; i++) {
                                if (getFrameId().equals(relationalPagesId[i])) {
                                    hasExited = true;
                                    break;
                                }

                            }

                            if (hasExited == true) {//已存在
                                Message msg = getHandler().obtainMessage();
                                msg.what = EXISTED;
                                msg.arg1=getRelationIndex();
                                getHandler().sendMessage(msg);
                            } else {//不存在
                                int page_idsLength;
                                if (relationalPagesId.length == 1 && relationalPagesId[0].equals(""))//如果是原本没有相关页面的话
                                    page_idsLength = 1;
                                else
                                    page_idsLength = 1 + relationalPagesId.length;
                                String[] page_ids = new String[page_idsLength];

                                int pageIdsIndex = 0;
                                //把新选的和原来的拼接起来
                                page_ids[pageIdsIndex++] = getFrameId();
                                //之所以采用page_idsLength-chosenPageIds.size()来判断是为了防止原本没有页面这种情况（这种情况下relationalPagesId.length仍为1）
                                for (int j = 0; j < page_idsLength - 1; j++) {
                                    page_ids[pageIdsIndex] = relationalPagesId[j];
                                    pageIdsIndex++;
                                }


                                RequestData deployBatchOfDevicesRequestData = new RequestData(null, null, getDevice_id(), getMajor(), page_ids, null, getMinor(), getUuid(), null, null, null, null, null);

                                ConnectRequest deployBatchOfDevicesConnectrequest = new ConnectRequest(getToken(), getDeployBatchOfDeviceSubListAdapter(), getDeployBatchOfDeviceSubList(),
                                        getThisDeployBatchOfDeviceFrame(), getHandler(), getRelationIndex());

                                deployBatchOfDevicesConnectrequest.RequestAPI(RequestKind.DeployPages, deployBatchOfDevicesRequestData);

                            }


                        }

                    } catch (JSONException e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
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
                    //如果部署不成功
                    Message msg = new Message();
                    msg.what = FAILED;
                    getHandler().sendMessage(msg);
                }


            }
        });
    }
}
