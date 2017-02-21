package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainFrame extends Activity {

    private final static int EDITDEVICE_RESULT_CODE = 3;
    private final static int ADDPAGE_REQUEST_CODE = 4;
    private final static int ADDPAGE_RESULT_CODE = 4;
    private final static int EDITDEVICEREPLYPAGE_RESULT_CODE = 12;
    private final static int BOTHEDIT_RESULT_CODE = 13;

    private final static int EDITPAGE_RESULT_CODE = 14;

    private final static int ADD_PAGE_RESULT_CODE = 18;

    private List<Map<String, Object>> myDeviceList;
    private RefreshableListView myDevicelv, frameLibLv;//这俩是要用到下拉更新效果的
    private MydeviceListAdapter myDeviceAdapter;

    private FrameLibAdapter frameLibAdapter;
    private List<Map<String, Object>> frameLibList;

    private MyInfoAdapter myInfoAdapter;
    ;
    private ListView myInfoLv;

    private MainFrame thisFrame;

    private String chosenOfficialAccountName;
    private String chosenOfficialAccountHint;
    private String accountName;
    private String chosenOfficialAccountAffliatedCareer;
    private String token;
    private Bitmap chosenOfficialAccountImage;
    private byte[] chosenOfficialAccountImageBytes;
    private String chosenOfficialAccountHeadImageURl;

    private ActionBar bar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);

        this.thisFrame = this;

        //自定义actionbar
        bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_mydevice);

        Intent intent = getIntent();

        chosenOfficialAccountName = intent.getStringExtra("chosenOfficialAccountName");
        chosenOfficialAccountHint = intent.getStringExtra("chosenOfficialAccountHint");
        accountName = intent.getStringExtra("accountName");
        chosenOfficialAccountAffliatedCareer = intent.getStringExtra("chosenOfficialAccountAffliatedCareer");
        token = intent.getStringExtra("token");

        boolean imageIsURLFlag = intent.getBooleanExtra("imageIsURL", false);
        if (imageIsURLFlag == false) {//这是从选择账户界面跳过来的
            chosenOfficialAccountImageBytes = intent.getByteArrayExtra("chosenOfficialAccountImageBytes");
            chosenOfficialAccountImage = BitmapFactory.decodeByteArray(chosenOfficialAccountImageBytes, 0,
                    chosenOfficialAccountImageBytes.length);
        } else {//这是直接从splash跳过来的
            chosenOfficialAccountHeadImageURl = intent.getStringExtra("chosenOfficialAccountHeadImageURl");
            chosenOfficialAccountImage = null;

            //检查网络连接
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                //不能连接到
                AlertDialog.Builder builder3 = new AlertDialog.Builder(thisFrame);
                builder3.setTitle("提示");
                builder3.setCancelable(false);
                builder3.setMessage("您已断开了与互联网的连接，请重新连接后重试");
                builder3.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog networkLinkDialog = builder3.create();
                networkLinkDialog.show();
                return;

            }
        }

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        LinearLayout deviceListLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tabhost_devicelist_layout, null);
        tabHost.addTab(tabHost.newTabSpec("myDevice").setIndicator(deviceListLayout).setContent(R.id.tab1));


        LinearLayout frameLibLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tabhost_framelib_layout, null);
        tabHost.addTab(tabHost.newTabSpec("pageLib").setIndicator(frameLibLayout).setContent(R.id.tab2));

        LinearLayout myInfoLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tabhost_myinfo_layout, null);
        tabHost.addTab(tabHost.newTabSpec("me").setIndicator(myInfoLayout).setContent(R.id.tab3));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
                switch (tabId) {
                    case "myDevice":
                        bar.setCustomView(R.layout.actionbar_layout_mydevice);
                        ActionbarSegment actionbarSegment = (ActionbarSegment) bar.getCustomView().findViewById(R.id.actionbarSegment);
                        actionbarSegment.setMydevicelv(myDevicelv);
                        actionbarSegment.setMydeviceListAdapter(myDeviceAdapter);
                        actionbarSegment.setThisMainFrame(thisFrame);
                        break;
                    case "pageLib":
                        bar.setCustomView(R.layout.actionbar_layout_framelib);
                        TextView frameLibTitle = (TextView) bar.getCustomView().findViewById(R.id.frameLibTitle);
                        frameLibTitle.setText("页面库");
                        Button addFrameBtn = (Button) bar.getCustomView().findViewById(R.id.addFrameBtn);
                        addFrameBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra("token", token);
                                intent.putExtra("title", "添加页面");
                                intent.putExtra("operationType", "addPage");
                                intent.setClass(thisFrame, AddOrEditPageFrame.class);
                                thisFrame.startActivityForResult(intent, ADDPAGE_REQUEST_CODE);
                            }
                        });
                        break;
                    case "me":
                        bar.setCustomView(R.layout.actionbar_layout_myinfo);
                        TextView myInfoTitle = (TextView) bar.getCustomView().findViewById(R.id.myInfoTitle);
                        myInfoTitle.setText("公众号信息");
                        break;
                }
            }
        });


        //“我的设备”页面
        myDevicelv = (RefreshableListView) findViewById(R.id.myDeviceLv);
        myDeviceList = new ArrayList<Map<String, Object>>();
        myDeviceAdapter = new MydeviceListAdapter(myDeviceList, this, token);
        RequestData myDeviceRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
        ConnectRequest myDeviceConnectrequest = new ConnectRequest(token, myDevicelv, myDeviceAdapter, thisFrame);

        myDeviceConnectrequest.RequestAPI(RequestKind.SearchDevice, myDeviceRequestData);

        //“页面库”页面
        frameLibLv = (RefreshableListView) findViewById(R.id.frameLibLv);

        frameLibList = new ArrayList<Map<String, Object>>();
        frameLibAdapter = new FrameLibAdapter(frameLibList, frameLibLv, thisFrame, token);

        RequestData frameLibRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
        ConnectRequest frameLibConnectrequest = new ConnectRequest(token, frameLibAdapter, frameLibLv, thisFrame);

        frameLibConnectrequest.RequestAPI(RequestKind.SearchPageList, frameLibRequestData);


        //“我”页面
        ArrayList<HashMap<String, Object>> showTextList;
        showTextList = getData();
        myInfoAdapter = new MyInfoAdapter(this, showTextList);
        myInfoLv = (ListView) findViewById(R.id.myInfoList);
        myInfoLv.setAdapter(myInfoAdapter);

        //如果是存储在本地的，就要通过URL重新获取头像
        if (imageIsURLFlag == true) {
            GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(chosenOfficialAccountHeadImageURl, myInfoAdapter, myInfoLv);
            getImageAsyncTask.execute();
        }

        //获取用户有效期和账户余额
        String URL = "http://api.wxyaoyao.com/3/user/profile?token=" + token;
        UserProfileAsyncTask userProfileAsyncTask = new UserProfileAsyncTask(URL, token, myInfoAdapter, myInfoLv, thisFrame);
        userProfileAsyncTask.execute();

        ActionbarSegment actionbarSegment = (ActionbarSegment) bar.getCustomView().findViewById(R.id.actionbarSegment);
        actionbarSegment.setMydevicelv(myDevicelv);
        actionbarSegment.setMydeviceListAdapter(myDeviceAdapter);
        actionbarSegment.setThisMainFrame(thisFrame);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlertDialog.Builder builder;
        Bundle bundle;
        String newDeviceName;
        int deviceIndexInList;
        int framesNumberOnThisDevice;

        SharedPreferences mySharedPreferences;
        SharedPreferences.Editor editor;
        String[] relationalPagesId;
        String relationPagesIdSerialize;
        switch (resultCode) {
            case EDITDEVICE_RESULT_CODE:
                builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("保存更改成功"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //参数都设置完成了，创建并显示出来
                builder.create().show();

                bundle = data.getExtras();
                newDeviceName = bundle.getString("newDeviceName");
                deviceIndexInList = bundle.getInt("deviceIndexInList");
                myDeviceAdapter.setItem(deviceIndexInList, "deviceName", newDeviceName);
                myDevicelv.setAdapter(myDeviceAdapter, null, null, null, null);
                break;
            case EDITDEVICEREPLYPAGE_RESULT_CODE:
                builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("保存更改成功"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //参数都设置完成了，创建并显示出来
                builder.create().show();
                bundle = data.getExtras();
                framesNumberOnThisDevice = bundle.getInt("framesNumberOnThisDevice");
                deviceIndexInList = bundle.getInt("deviceIndexInList");
                myDeviceAdapter.setItem(deviceIndexInList, "deviceHint", framesNumberOnThisDevice + "个页面");


                //改写缓存
                mySharedPreferences = getSharedPreferences("pageNumberCache",
                        Activity.MODE_PRIVATE);
                editor = mySharedPreferences.edit();
                editor.putString("pageNumberOfDevice" + deviceIndexInList, "" + framesNumberOnThisDevice);

                relationalPagesId = bundle.getStringArray("relationalPagesIdArray");

                myDeviceAdapter.setItemsAtMap(deviceIndexInList, "relationalPagesIdArray", relationalPagesId);
                myDevicelv.setAdapter(myDeviceAdapter, null, null, null, null);

                relationPagesIdSerialize = relationalPagesId[0];
                for (int i = 1; i < relationalPagesId.length; i++) {
                    relationPagesIdSerialize += "@";
                    relationPagesIdSerialize += relationalPagesId[i];
                }
                editor.putString("relationalPagesIdArrayOfDevice" + deviceIndexInList, relationPagesIdSerialize);
                editor.commit();
                break;
            case BOTHEDIT_RESULT_CODE:
                builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("保存更改成功"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //参数都设置完成了，创建并显示出来
                builder.create().show();

                bundle = data.getExtras();
                newDeviceName = bundle.getString("newDeviceName");
                deviceIndexInList = bundle.getInt("deviceIndexInList");
                framesNumberOnThisDevice = bundle.getInt("framesNumberOnThisDevice");
                myDeviceAdapter.setItem(deviceIndexInList, "deviceName", newDeviceName);
                myDeviceAdapter.setItem(deviceIndexInList, "deviceHint", framesNumberOnThisDevice + "个页面");
                myDevicelv.setAdapter(myDeviceAdapter, null, null, null, null);

                //改写缓存
                mySharedPreferences = getSharedPreferences("pageNumberCache",
                        Activity.MODE_PRIVATE);
                editor = mySharedPreferences.edit();
                editor.putString("pageNumberOfDevice" + deviceIndexInList, "" + framesNumberOnThisDevice);

                relationalPagesId = bundle.getStringArray("relationalPagesIdArray");
                relationPagesIdSerialize = relationalPagesId[0];
                for (int i = 1; i < relationalPagesId.length; i++) {
                    relationPagesIdSerialize += "@";
                    relationPagesIdSerialize += relationalPagesId[i];
                }
                editor.putString("relationalPagesIdArrayOfDevice" + deviceIndexInList, relationPagesIdSerialize);
                editor.commit();
                break;
            case EDITPAGE_RESULT_CODE:
                builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("修改页面信息成功！"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //参数都设置完成了，创建并显示出来
                builder.create().show();

                bundle = data.getExtras();
                byte[] newThumbnailBytesArray = bundle.getByteArray("newThumbnailBytesArray");
                Bitmap newThumbnailBitmap = BitmapFactory.decodeByteArray(newThumbnailBytesArray, 0,
                        newThumbnailBytesArray.length);
                String newIconUrl = bundle.getString("newIconUrl");
                String newPageName = bundle.getString("newPageName");
                String newMainTitle = bundle.getString("newMainTitle");
                String newSubTitle = bundle.getString("newSubTitle");
                String newPageLink = bundle.getString("newPageLink");
                int editIndex = bundle.getInt("editIndex");

                frameLibAdapter.setItemAtMap(editIndex, "frameImage", newThumbnailBitmap);
                frameLibAdapter.setItemAtMap(editIndex, "frameImageType", "bitmap");
                frameLibAdapter.setItemAtMap(editIndex, "iconUrl", newIconUrl);
                frameLibAdapter.setItemAtMap(editIndex, "frameName", newMainTitle);
                frameLibAdapter.setItemAtMap(editIndex, "frameHint", newSubTitle);
                frameLibAdapter.setItemAtMap(editIndex, "pageName", newPageName);
                frameLibAdapter.setItemAtMap(editIndex, "pageLink", newPageLink);
                frameLibLv.setAdapter(null, frameLibAdapter, null, null, null);

                break;
            case ADD_PAGE_RESULT_CODE:
                builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("新增页面成功，加入的页面位于列表尾端，请先刷新！"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //参数都设置完成了，创建并显示出来
                builder.create().show();
                bundle = data.getExtras();

                byte[] thumbnailBytesArray = bundle.getByteArray("thumbnailBytes");
                Bitmap thumbnailBitmap = BitmapFactory.decodeByteArray(thumbnailBytesArray, 0,
                        thumbnailBytesArray.length);
                String iconUrl = bundle.getString("icon_url");
                String pageName = bundle.getString("pageName");
                String mainTitle = bundle.getString("mainTitle");
                String subTitle = bundle.getString("subTitle");
                String pageLink = bundle.getString("pageLink");

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("frameImage", thumbnailBitmap);
                map.put("frameImageType", "bitmap");
                map.put("iconUrl", iconUrl);
                map.put("frameName", mainTitle);
                map.put("frameHint", subTitle);
                map.put("pageName", pageName);
                map.put("pageLink", pageLink);
                frameLibAdapter.addItem(map);

                frameLibLv.setAdapter(null, frameLibAdapter, null, null, null);

                break;

        }
    }

    //自定义的list数据
    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("staticInfo1", "绑定公众号");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("chosenOfficialAccountName", chosenOfficialAccountName);//这是公众号的名字
        map.put("chosenOfficialAccountHint", chosenOfficialAccountHint);
        map.put("chosenOfficialAccountImage", chosenOfficialAccountImage);
        map.put("affliatedCareer", "所属行业：" + chosenOfficialAccountAffliatedCareer);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("staticInfo2", "账号");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("accountName", accountName);//这是登陆账号
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("staticInfo3", "账户信息");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("ValidDateTitle", "有效期至：");
        map.put("ValidDate", " 年　月　日");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("myFinanceRemainTitle", "我的账户余额");
        map.put("myFinanceRemain", " 元");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("exitBtnText", "退出登录");
        list.add(map);
        return list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
