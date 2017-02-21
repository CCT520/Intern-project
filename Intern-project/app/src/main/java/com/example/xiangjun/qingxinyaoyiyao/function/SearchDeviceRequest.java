package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceSegment;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceSubListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.MydeviceListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class SearchDeviceRequest {

    private static final int EXISTED = 0;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;


    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=device_search";
    private String token;
    private String begin;
    private String count;
    private String type;
    private String device_id;
    private String minor;
    private String major;
    private String uuid;
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();
    private String result = null;
    private RequestData rd;
    private MydeviceListAdapter mydeviceListAdapter;
    private RefreshableListView myDevicelv;
    private MainFrame thisMainFrame;
    private float origin_x, later_x;

    //下面是“部署到设备”页面获取的设备列表用到的变量
    private DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter;
    private RefreshableListView deployBatchOfDeviceSubList;
    private DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame;
    private DeployBatchOfDeviceSegment deployBatchOfDeviceSegment;
    private Button chooseAllBtn;
    private Button startDeployBtn;
    private String frameId;
    private AlertDialog isDeployingDialog;

    private int existedNumber = 0;
    private int deploySuccessfullyNumber = 0;
    private int deployFailedNumber = 0;
    private int userChosedNumber = 0;


    private static ArrayList<Integer> deployFailedIndex = new ArrayList<Integer>();
    private static ArrayList<Integer> deploySuccessfullyIndexPageNumber = new ArrayList<Integer>();//用来存储部署成功的设备对应的页面数
    private ArrayList<Integer> selectedIndexList = new ArrayList<Integer>();
    private ArrayList<Integer> successList = new ArrayList<Integer>();
    private ArrayList<Integer> existedList = new ArrayList<Integer>();
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case EXISTED:
                    existedNumber++;
                    getDeployBatchOfDeviceSubListAdapter().addInExistedList(msg.arg1);
                    break;
                case SUCCESS:
                    deploySuccessfullyNumber++;
                    getDeployBatchOfDeviceSubListAdapter().addInSuccessList(msg.arg1);
                    deploySuccessfullyIndexPageNumber.add(msg.arg2);
                    getDeployBatchOfDeviceSubListAdapter().addIndeploySuccessfullyIndexPageNumberList(msg.arg2);
                    break;
                case FAILED:
                    deployFailedNumber++;
                    deployFailedIndex.add(msg.arg1);
                    break;
            }

            successList = getDeployBatchOfDeviceSubListAdapter().getHasDeployedSuccefullyIndex();
            existedList = getDeployBatchOfDeviceSubListAdapter().getExistedIndexList();

            if (selectedIndexList.size() == successList.size() + deployFailedNumber + existedList.size()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
                builder.setTitle("提示"); //设置标题
                builder.setMessage("已成功部署" + successList.size() + "个设备\n" +
                        "部署失败" + deployFailedNumber + "个设备\n" +
                        "忽略重复部署设备" + existedList.size() + "个\n" +
                        "部署失败的设备保留了选中"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (chooseAllBtn.getText().equals("全选以下设备")) {//如果不是全选的情况
                            for (int i = 0; i < successList.size(); i++) {

                                if (getDeployBatchOfDeviceSubListAdapter().isInSuccessList(successList.get(i))) {
                                    HashMap<String, Object> tempMap =
                                            (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(i);
                                    ImageView tempItemImage = (ImageView) tempMap.get("chooseFlagView");
                                    tempItemImage.clearAnimation();
                                    tempItemImage.setVisibility(View.GONE);

                                    getDeployBatchOfDeviceSubListAdapter().setItem(successList.get(i), "deployBatchOfDeviceSubListItemNumber",
                                            deploySuccessfullyIndexPageNumber.get(i) + "个页面");

                                    getDeployBatchOfDeviceSubList().setAdapter(null, null, getDeployBatchOfDeviceSubListAdapter(), null, null);

                                }
                            }
                            successList.clear();
                            selectedIndexList.clear();
                        } else {//如果是全选的情况
                            for (int i = 0; i < getDeployBatchOfDeviceSubListAdapter().getMaxIndex() + 1; i++) {

                                if (getDeployBatchOfDeviceSubListAdapter().isInSuccessList(i)) {
                                    HashMap<String, Object> tempMap =
                                            (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(i);
                                    ImageView tempItemImage = (ImageView) tempMap.get("chooseFlagView");
                                    tempItemImage.clearAnimation();
                                    tempItemImage.setVisibility(View.GONE);

                                    getDeployBatchOfDeviceSubListAdapter().setItem(i, "deployBatchOfDeviceSubListItemNumber",
                                            deploySuccessfullyIndexPageNumber.get(i) + "个页面");

                                    getDeployBatchOfDeviceSubList().setAdapter(null, null, getDeployBatchOfDeviceSubListAdapter(), null, null);
                                }
                            }
                        }

                        for (int i = 0; i < getDeployBatchOfDeviceSubListAdapter().getMaxIndex() + 1; i++) {

                            if (getDeployBatchOfDeviceSubListAdapter().isInExistedList(i)) {
                                HashMap<String, Object> tempMap =
                                        (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(i);
                                ImageView tempItemImage = (ImageView) tempMap.get("chooseFlagView");
                                tempItemImage.clearAnimation();
                                tempItemImage.setVisibility(View.GONE);
                            }
                        }

                        getDeployBatchOfDeviceSubListAdapter().setChooseAllBtnHasBeenClicked(false);

                        if (getChooseAllBtn().getText().equals("取消全选设备"))
                            getChooseAllBtn().setText("全选以下设备");

                        deploySuccessfullyIndexPageNumber.clear();//显示完后把这两个数组清空，以免后面再进来的时候显示错误

                        isDeployingDialog.dismiss();


                    }
                });
                //参数都设置完成了，创建并显示出来
                builder.create().show();
            }
        }
    };

    public android.os.Handler getHandler() {
        return handler;
    }

    public String getBegin() {
        return begin;
    }

    public String getCount() {
        return count;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getMinor() {
        return minor;
    }

    public String getMajor() {
        return major;
    }

    public String getUuid() {
        return uuid;
    }

    public String getToken() {
        return token;
    }

    public RequestData getRd() {
        return rd;
    }

    public MydeviceListAdapter getMydeviceListAdapter() {
        return mydeviceListAdapter;
    }

    public RefreshableListView getMyDevicelv() {
        return myDevicelv;
    }

    public MainFrame getThisMainFrame() {
        return thisMainFrame;
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

    public DeployBatchOfDeviceSegment getDeployBatchOfDeviceSegment() {
        return deployBatchOfDeviceSegment;
    }

    public Button getChooseAllBtn() {
        return chooseAllBtn;
    }

    public Button getStartDeployBtn() {
        return startDeployBtn;
    }


    public String getFrameId() {
        return frameId;
    }

    //这是在主界面查询设备列表用到的构造函数
    public SearchDeviceRequest(String token, RequestData rd, MydeviceListAdapter mydeviceListAdapter,
                               RefreshableListView myDevicelv, MainFrame thisMainFrame) {
        // TODO Auto-generated constructor stub
        //this.url=rd.getUrl();
        this.token = token;
        this.rd = rd;
        this.mydeviceListAdapter = mydeviceListAdapter;
        this.myDevicelv = myDevicelv;
        this.thisMainFrame = thisMainFrame;
        this.begin = rd.getBegin();
        this.count = rd.getCount();
        this.type = rd.getType();
        this.device_id = rd.getDevice_id();
        this.major = rd.getMajor();
        this.minor = rd.getMinor();
        this.uuid = rd.getUuid();
        this.url = url + "&token=" + this.token;
        if (type == "2") {
            //search-device 2       Ok!
            params.put("type", this.type);
            params.put("begin", this.begin);
            params.put("count", this.count);
        } else {
            //search-device 1       OK!
            Map<String, String> map = new HashMap<String, String>();
            map.put("device_id", device_id);
            map.put("uuid", uuid);
            map.put("major", major);
            map.put("minor", minor);
            params.put("type", "1");
            params.put("device_identifiers", map);
        }
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

                        int err_code = responseObjec.getInt("err_code");
                        if (err_code == 0) {
                            JSONObject data = responseObjec.getJSONObject("data");
                            final JSONArray devices = data.getJSONArray("devices");
                            int totalCount = data.getInt("total_count");//totalCount是服务器端总共有的设备数

                            if (getMydeviceListAdapter().getLastLoadingMaxIndex() + devices.length() == totalCount)
                                getMydeviceListAdapter().setHasFinishedLoading(true);

                            for (int i = 0; i < devices.length(); i++) {//devices.length()是一次获取的设备数
                                //获取相关参数
                                JSONObject device_i = devices.getJSONObject(i);
                                String deviceName = device_i.getString("comment");

                                int status = device_i.getInt("status");//是否激活,0未激活，1激活

                                Map<String, Object> myDeviceMap = new HashMap<String, Object>();
                                //添加进list里
                                myDeviceMap.put("deviceName", deviceName);

                                if (status == 1) {
                                    myDeviceMap.put("isActiviatedImage", R.drawable.is_activiated_img);
                                    myDeviceMap.put("isActiciated", true);
                                } else {
                                    myDeviceMap.put("isActiviatedImage", R.drawable.is_not_activiated_img);
                                    myDeviceMap.put("isActiciated", false);
                                }
                                myDeviceMap.put("deviceHint", "页面数量：加载中");
                                myDeviceMap.put("token", getToken());
                                getMydeviceListAdapter().addItem(myDeviceMap);
                            }

                            for (int i = 0; i < devices.length(); i++) {
                                //获取相关参数
                                JSONObject device_i = devices.getJSONObject(i);
                                String uuid = device_i.getString("uuid");
                                String deviceId = String.valueOf(device_i.getInt("device_id"));
                                String major = String.valueOf(device_i.getInt("major"));
                                String minor = String.valueOf(device_i.getInt("minor"));

                                getMydeviceListAdapter().addItemAtMap(i, "major", major);
                                getMydeviceListAdapter().addItemAtMap(i, "minor", minor);
                                getMydeviceListAdapter().addItemAtMap(i, "device_id", deviceId);
                                getMydeviceListAdapter().addItemAtMap(i, "uuid", uuid);

                                SharedPreferences mySharedPreferences = getThisMainFrame().getSharedPreferences("pageNumberCache",
                                        Activity.MODE_PRIVATE);
                                Boolean pageNumberIsCached = mySharedPreferences.getBoolean("pageNumberIsCached", false);
                                if (pageNumberIsCached == false) {//未缓存页面数
                                    RequestData myDeviceRequestData = new RequestData(getBegin(), String.valueOf(devices.length()), deviceId, major, null, "1", minor, uuid, null, null, null, null, null);
                                    ConnectRequest myDeviceConnectrequest = new ConnectRequest(getToken(), getMydeviceListAdapter(), getMyDevicelv(), i, getThisMainFrame());
                                    myDeviceConnectrequest.RequestAPI(RequestKind.RelationSearch, myDeviceRequestData);
                                } else {//已缓存页面数
                                    //先判断是否过期
                                    long cachedTime = mySharedPreferences.getLong("pageNumberCachedTime", 0);
                                    Date nowDate = new Date();
                                    long nowTime = nowDate.getTime();
                                    if (nowTime - cachedTime > 20 * 60 * 1000) {//如果超过了20分钟，则删除缓存，重新获取
                                        mySharedPreferences.edit().clear().commit();

                                        //这里的传的begin和count是为了后面设置上次加载最大index
                                        RequestData myDeviceRequestData = new RequestData(getBegin(), String.valueOf(devices.length()), deviceId, major, null, "1", minor, uuid, null, null, null, null, null);
                                        ConnectRequest myDeviceConnectrequest = new ConnectRequest(getToken(), getMydeviceListAdapter(), getMyDevicelv(), i, getThisMainFrame());
                                        myDeviceConnectrequest.RequestAPI(RequestKind.RelationSearch, myDeviceRequestData);
                                    } else {//若未过期则直接读缓存
                                        String pageCount = mySharedPreferences.getString("pageNumberOfDevice" + i, "");
                                        if (pageCount.equals("0")) {
                                            getMydeviceListAdapter().setItem(i, "deviceHint", "未绑定页面");
                                            getMyDevicelv().setAdapter(getMydeviceListAdapter(), null, null, null, null);
                                            getMyDevicelv().setOnRefreshMyDeviceListCompleteParams(getToken(), getMyDevicelv(), getThisMainFrame());
                                            getMydeviceListAdapter().setLastLoadingMaxIndex(Integer.valueOf(getBegin()) + devices.length() - 1);
                                        } else {
                                            String[] relationalPagesId = mySharedPreferences.getString("relationalPagesIdArrayOfDevice" + i, "").split("@");
                                            getMydeviceListAdapter().setItem(i, "deviceHint", pageCount + "个页面");
                                            getMydeviceListAdapter().addItemsAtMap(i, "relationalPagesIdArray", relationalPagesId);
                                            getMyDevicelv().setAdapter(getMydeviceListAdapter(), null, null, null, null);
                                            getMyDevicelv().setOnRefreshMyDeviceListCompleteParams(getToken(), getMyDevicelv(), getThisMainFrame());
                                            getMydeviceListAdapter().setLastLoadingMaxIndex(Integer.valueOf(getBegin()) + devices.length() - 1);
                                        }

                                        if(getMyDevicelv().getMyDeviceStateBeforeRefreshing()!=null)
                                            getMyDevicelv().onRestoreInstanceState(getMyDevicelv().getMyDeviceStateBeforeRefreshing());

                                    }


                                }


                            }


                            getMyDevicelv().setonRefreshListener(new RefreshableListView.OnRefreshListener() {

                                @Override
                                public void onRefresh() {
                                    new AsyncTask<Void, Void, Void>() {
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void result) {
                                            getMyDevicelv().setMyDeviceStateBeforeRefreshing(getMyDevicelv().onSaveInstanceState());//用于防止更新完后自动跳回顶部
                                            getMyDevicelv().setOnRefreshMyDeviceListCompleteParams(getToken(), getMyDevicelv(), getThisMainFrame());
                                            getMydeviceListAdapter().notifyDataSetChanged();
                                            getMyDevicelv().onRefreshComplete();
                                        }
                                    }.execute(null, null, null);
                                }
                            });
                        } else {
                            String err_msg = responseObjec.getString("err_msg");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                            builder.setTitle("提示");
                            builder.setMessage("在加载设备列表时发生了未知错误\n" +
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


                    } catch (UnsupportedEncodingException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                        builder.setTitle("提示");
                        builder.setMessage("在加载设备列表时出现UnsupportedEncoding异常，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                        builder.setTitle("提示");
                        builder.setMessage("在加载设备列表时JSON包解析出错，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                    builder.setTitle("提示");
                    builder.setMessage("在加载设备列表时网络异常，请检查！");
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

    //这是在部署页面上的查询设备列表需要用的构造函数
    public SearchDeviceRequest(String token, RequestData rd, DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter,
                               RefreshableListView deployBatchOfDeviceSubList, DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,
                               DeployBatchOfDeviceSegment deployBatchOfDeviceSegment, final Button chooseAllBtn, final Button startDeployBtn,
                               String frameId) {
        // TODO Auto-generated constructor stub
        this.token = token;
        this.rd = rd;
        this.deployBatchOfDeviceSubListAdapter = deployBatchOfDeviceSubListAdapter;
        this.deployBatchOfDeviceSubList = deployBatchOfDeviceSubList;
        this.thisDeployBatchOfDeviceFrame = thisDeployBatchOfDeviceFrame;
        this.deployBatchOfDeviceSegment = deployBatchOfDeviceSegment;
        this.chooseAllBtn = chooseAllBtn;
        this.startDeployBtn = startDeployBtn;
        this.frameId = frameId;
        this.begin = rd.getBegin();
        this.count = rd.getCount();
        this.type = rd.getType();
        this.device_id = rd.getDevice_id();
        this.major = rd.getMajor();
        this.minor = rd.getMinor();
        this.uuid = rd.getUuid();
        this.url = url + "&token=" + this.token;
        if (type == "2") {

            params.put("type", this.type);
            params.put("begin", this.begin);
            params.put("count", this.count);
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put("device_id", this.device_id);
            map.put("uuid", this.uuid);
            map.put("major", this.major);
            map.put("minor", this.minor);
            params.put("type", "1");
            params.put("device_identifiers", map);
        }
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
                        JSONObject data = responseObjec.getJSONObject("data");
                        JSONArray devices = data.getJSONArray("devices");


                        //动态设置listview的高度
                        final float scale = getThisDeployBatchOfDeviceFrame().getResources().getDisplayMetrics().scaledDensity;
                        int height;
                        height = (int) (devices.length() * 30 * scale);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        getDeployBatchOfDeviceSubList().setLayoutParams(params);


                        //动态设置listview的高度

                        int totalCount = data.getInt("total_count");//totalCount是服务器端总共有的设备数

                        if (devices.length() == totalCount)
                            getDeployBatchOfDeviceSubListAdapter().setHasFinishedLoading(true);

                        for (int i = 0; i < devices.length(); i++) {//devices.length()是一次获取的设备数
                            //获取相关参数
                            JSONObject device_i = devices.getJSONObject(i);
                            String deviceName = device_i.getString("comment");

                            Map<String, Object> deployBatchOfDevicesSubListMap = new HashMap<String, Object>();
                            //添加进list里
                            deployBatchOfDevicesSubListMap.put("deployBatchOfDeviceSubListItemName", deviceName);
                            deployBatchOfDevicesSubListMap.put("chooseFlagResource", R.drawable.choose_flag);
                            deployBatchOfDevicesSubListMap.put("deployBatchOfDeviceSubListItemNumber", "加载中...");

                            getDeployBatchOfDeviceSubListAdapter().addItem(deployBatchOfDevicesSubListMap);
                        }

                        for (int i = 0; i < devices.length(); i++) {
                            //获取相关参数
                            JSONObject device_i = devices.getJSONObject(i);
                            String deviceId = String.valueOf(device_i.getInt("device_id"));
                            String uuid = device_i.getString("uuid");
                            String major = String.valueOf(device_i.getInt("major"));
                            String minor = String.valueOf(device_i.getInt("minor"));

                            getDeployBatchOfDeviceSubListAdapter().addItemAtMap(i, "device_id", deviceId);
                            getDeployBatchOfDeviceSubListAdapter().addItemAtMap(i, "uuid", uuid);
                            getDeployBatchOfDeviceSubListAdapter().addItemAtMap(i, "major", major);
                            getDeployBatchOfDeviceSubListAdapter().addItemAtMap(i, "minor", minor);

                            RequestData deployOnDeviceRequestData = new RequestData(getBegin(), getCount(), deviceId, major, null, "1", minor, uuid, null, null, null, null, null);
                            ConnectRequest deployOnDeviceConnectrequest = new ConnectRequest(getToken(), getThisDeployBatchOfDeviceFrame(), getDeployBatchOfDeviceSubListAdapter(),
                                    getDeployBatchOfDeviceSubList(), i);
                            deployOnDeviceConnectrequest.RequestAPI(RequestKind.RelationSearch, deployOnDeviceRequestData);
                        }


                        getDeployBatchOfDeviceSubList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HashMap<String, Object> selectedItemMap =
                                        (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(position - 1);
                                ImageView selectedItemImage = (ImageView) selectedItemMap.get("chooseFlagView");
                                selectedItemImage.clearAnimation();

                                if (selectedItemImage.getVisibility() == View.GONE) {
                                    selectedItemImage.setVisibility(View.VISIBLE);
                                    getDeployBatchOfDeviceSubListAdapter().addInSelectedList(position - 1);
                                } else {
                                    selectedItemImage.setVisibility(View.GONE);
                                    getDeployBatchOfDeviceSubListAdapter().deleteSelectedIndex(position - 1);
                                }

                                parent.requestLayout();

                            }
                        });

                        getChooseAllBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (getChooseAllBtn().getText().equals("全选以下设备")) {
                                    int i;
                                    for (i = 0; i < getDeployBatchOfDeviceSubListAdapter().getMaxIndex() + 1; i++) {
                                        HashMap<String, Object> tempMap =
                                                (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(i);
                                        ImageView tempItemImage = (ImageView) tempMap.get("chooseFlagView");
                                        tempItemImage.clearAnimation();
                                        tempItemImage.setVisibility(View.VISIBLE);
                                        getDeployBatchOfDeviceSubListAdapter().addInSelectedList(i);
                                    }
                                    for (; i < getDeployBatchOfDeviceSubListAdapter().getCount(); i++) {
                                        getDeployBatchOfDeviceSubListAdapter().addInSelectedList(i);
                                    }
                                    getDeployBatchOfDeviceSubListAdapter().setChooseAllBtnHasBeenClicked(true);
                                    getChooseAllBtn().setText("取消全选设备");
                                } else if (getChooseAllBtn().getText().equals("取消全选设备")) {
                                    for (int i = 0; i < getDeployBatchOfDeviceSubListAdapter().getMaxIndex() + 1; i++) {
                                        HashMap<String, Object> tempMap =
                                                (HashMap) getDeployBatchOfDeviceSubListAdapter().
                                                        getItem(i);
                                        ImageView tempItemImage = (ImageView) tempMap.get("chooseFlagView");
                                        tempItemImage.clearAnimation();
                                        tempItemImage.setVisibility(View.GONE);
                                    }
                                    getDeployBatchOfDeviceSubListAdapter().clearSelectedList();
                                    getChooseAllBtn().setText("全选以下设备");
                                    getDeployBatchOfDeviceSubListAdapter().setChooseAllBtnHasBeenClicked(false);
                                }

                            }
                        });

                        getStartDeployBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                selectedIndexList = getDeployBatchOfDeviceSubListAdapter().getSelectedIndexList();

                                if(!deployFailedIndex.isEmpty()){
                                    for(int i=0;i<deployFailedIndex.size();i++){
                                        selectedIndexList.add(deployFailedIndex.get(i));
                                    }
                                }

                                deploySuccessfullyIndexPageNumber.clear();
                                getDeployBatchOfDeviceSubListAdapter().clearSuccessList();
                                getDeployBatchOfDeviceSubListAdapter().clearExistedList();
                                getDeployBatchOfDeviceSubListAdapter().clearDeploySuccessfullyIndexPageNumberList();

                                //先检查是否选择

                                if (selectedIndexList.isEmpty()) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
                                    builder2.setTitle("提示"); //设置标题
                                    builder2.setMessage("尚未选择任何设备"); //设置内容
                                    //为了保证按钮的顺序一样，所以只能反过来设置
                                    builder2.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    //参数都设置完成了，创建并显示出来
                                    builder2.create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
                                    builder.setTitle("提示"); //设置标题
                                    builder.setMessage("开始部署到设备？"); //设置内容

                                    //为了保证按钮的顺序一样，所以只能反过来设置
                                    builder.setPositiveButton("不", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("部署", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            existedList.clear();
                                            successList.clear();

                                            deployFailedNumber = 0;

                                            deployFailedIndex.clear();

                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
                                            builder2.setMessage("正在部署中，请稍候..."); //设置内容
                                            builder2.setCancelable(false);
                                            isDeployingDialog = builder2.create();
                                            isDeployingDialog.show();


                                            String nowSelected = getDeployBatchOfDeviceSegment().getNowSelected();


                                            if (nowSelected.equals("addTv")) {//部署方式为新增


                                                for (int i = 0; i < selectedIndexList.size(); i++) {
                                                    HashMap<String, Object> tempMap =
                                                            (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(selectedIndexList.get(i));

                                                    String deviceId = (String) tempMap.get("device_id");
                                                    String uuid = (String) tempMap.get("uuid");
                                                    String major = (String) tempMap.get("major");
                                                    String minor = (String) tempMap.get("minor");

                                                    //直接从缓存中读取pagesId
                                                    RequestData deployOnDeviceRequestData = new RequestData(null, null, deviceId, major, null, "1", minor, uuid, null, null, null, null, null);
                                                    ConnectRequest deployOnDeviceConnectrequest = new ConnectRequest(getToken(), getThisDeployBatchOfDeviceFrame(), getDeployBatchOfDeviceSubListAdapter(),
                                                            getDeployBatchOfDeviceSubList(), getHandler(), selectedIndexList.get(i), getFrameId());
                                                    deployOnDeviceConnectrequest.RequestAPI(RequestKind.RelationSearch, deployOnDeviceRequestData);


                                                }

                                            } else if (nowSelected.equals("coverTv")) {//部署方式为覆盖(把以前的删除然后加上现在的)
                                                for (int i = 0; i < selectedIndexList.size(); i++) {
                                                    HashMap<String, Object> tempMap =
                                                            (HashMap) getDeployBatchOfDeviceSubListAdapter().getItem(selectedIndexList.get(i));

                                                    String deviceId = (String) tempMap.get("device_id");
                                                    String uuid = (String) tempMap.get("uuid");
                                                    String major = (String) tempMap.get("major");
                                                    String minor = (String) tempMap.get("minor");

                                                    String[] page_ids = {getFrameId()};
                                                    RequestData deployBatchOfDevicesRequestData = new RequestData(null, null, deviceId, major, page_ids, null, minor, uuid, null, null, null, null, null);

                                                    ConnectRequest deployBatchOfDevicesConnectrequest = new ConnectRequest(getToken(), getDeployBatchOfDeviceSubListAdapter(), getDeployBatchOfDeviceSubList(),
                                                            getThisDeployBatchOfDeviceFrame(), getHandler(), selectedIndexList.get(i));

                                                    deployBatchOfDevicesConnectrequest.RequestAPI(RequestKind.DeployPages, deployBatchOfDevicesRequestData);


                                                }
                                            }
                                        }
                                    });

                                    //参数都设置完成了，创建并显示出来
                                    builder.create().show();


                                }
                            }


                        });

                        getDeployBatchOfDeviceSubList().setonRefreshListener(new RefreshableListView.OnRefreshListener() {

                            @Override
                            public void onRefresh() {
                                new AsyncTask<Void, Void, Void>() {
                                    protected Void doInBackground(Void... params) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        getDeployBatchOfDeviceSubList().setDeployBatchOfDeviceSubListStateBeforeRefreshing(getDeployBatchOfDeviceSubList().onSaveInstanceState());//用于防止更新完后自动跳回顶部
                                        getDeployBatchOfDeviceSubList().setOnRefreshDeployOnDeviceListCompleteParams(getToken(),
                                                getDeployBatchOfDeviceSubList(), getThisDeployBatchOfDeviceFrame(),
                                                getDeployBatchOfDeviceSegment(), getChooseAllBtn(), getStartDeployBtn(), getFrameId());
                                        getDeployBatchOfDeviceSubListAdapter().notifyDataSetChanged();
                                        getDeployBatchOfDeviceSubList().onRefreshComplete();
                                    }
                                }.execute(null, null, null);
                            }
                        });


                    } catch (UnsupportedEncodingException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
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

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisDeployBatchOfDeviceFrame());
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


}

	


