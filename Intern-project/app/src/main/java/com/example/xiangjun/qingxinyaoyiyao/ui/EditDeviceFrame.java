package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.beacon.kit.SensoroBeaconConnection;
import com.sensoro.beacon.kit.SensoroBeaconConnectionV4;
import com.sensoro.beacon.kit.connection.BeaconConfiguration;
import com.sensoro.cloud.SensoroManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.sensoro.beacon.kit.Beacon.*;


public class EditDeviceFrame extends Activity {

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static int EDITNAME_REQUEST_CODE = 2;
    private final static int EDITNAME_RESULT_CODE = 2;

    private final static int ADDREPLYPAGE_REQUEST_CODE = 11;
    private final static int ADDREPLYPAGE_RESULT_CODE = 11;

    private final static int DELETE_PAGE_SUCCESS = 21;

    private EditDeviceAdapter editDeviceAdapter;
    private CancelParentListViewEvent editDeviceList;
    private String deviceId;
    private String uuid;
    private String deviceName;
    private String major;
    private String minor;
    private String token;
    private Bundle relationalPagesIdBundle;
    private String[] relationalPagesId;
    private int framesNumberOnThisDevice;

    private EditDeviceFrame thisFrame;
    private Button saveBtn;

    private ActionBar bar;

    private String newDeviceName = "";

    private int deviceIndexInList;

    private boolean hasEdittedDeviceName = false;
    private boolean hasEdittedReplyPage = false;

    private Handler deleteFrameHandler;

    //-----------这些是配置设备需要用的变量----------------------//
    private SensoroBeaconConnectionV4 sensorobeaconconnectionV4;
    private BeaconConfiguration beaconconfig;
    private SensoroBeaconConnection sensorobeaconconnection;
    private String serialnumber;
    private Beacon MyBeacon;
    private String textinfor;
    ArrayList<Beacon> beacons;
    BeaconManagerListener beaconManagerListener;
    SensoroManager sensoroManager;

    SensoroBeaconConnectionV4.WriteCallback writecallback = new SensoroBeaconConnectionV4.WriteCallback(){

        @Override
        public void onWriteFailure(int arg0) {
            // TODO Auto-generated method stub
            System.out.println("onWriteFailure:"+arg0);
        }

        @Override
        public void onWriteSuccess() {
            // TODO Auto-generated method stub
            System.out.println("onWriteSuccess:!!!!!");
        }

    };

    SensoroBeaconConnectionV4.BeaconConnectionCallback connectcallback = new  SensoroBeaconConnectionV4.BeaconConnectionCallback(){

        @Override
        public void onConnectedFailure(int arg0) {
            // TODO Auto-generated method stub
            System.out.println("onConnectedFailure "+arg0);
        }

        @Override
        public void onConnectedSuccess(Beacon arg0) {
            // TODO Auto-generated method stub
            System.out.println("onConnectedSuccess "+arg0);
        }

        @Override
        public void onDisconnected() {
            // TODO Auto-generated method stub

        }

    };

    //∏√Ω”ø⁄◊˜Œ™SensoroBeaconConnectionµƒ≤Œ ˝
    SensoroBeaconConnection.BeaconConnectionCallback beaconconnectioncallback =
            new SensoroBeaconConnection.BeaconConnectionCallback(){

                @Override//connect()÷¥–– ±±ªµ˜”√
                public void onConnectedState(Beacon arg0, int arg1, int arg2) {
                    // TODO Auto-generated method stub

                    Map<String,Object> majorMap=(HashMap)getEditDeviceAdapter().getItem(1);
                    int major=Integer.valueOf((String)majorMap.get("MajorMinorNumber"));

                    Map<String,Object> minorMap=(HashMap)getEditDeviceAdapter().getItem(2);
                    int minor=Integer.valueOf((String)minorMap.get("MajorMinorNumber"));

                    sensorobeaconconnection.writeMajorMinor(major, minor);
                }

                @Override
                public void onDisableAliBeacon(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDisableBackgroundEnhancement(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDisableIBeacon(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onDisablePassword(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onEnableAliBeacon(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onEnableBackgroundEnhancement(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onEnableIBeacon(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFlashLightWitCommand(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onReloadSensorData(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onRequireWritePermission(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onResetAcceleratorCount(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onResetToFactorySettings(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onUpdateAccelerometerCount(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onUpdateLightData(Beacon arg0, Double arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onUpdateMovingState(Beacon beacon, MovingState movingState) {

                }


                @Override
                public void onUpdateTemperatureData(Beacon arg0, Integer arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onWirteSensorSetting(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onWriteBaseSetting(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onWriteBroadcastKey(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }


                @Override//监听写入是否成功
                public void onWriteMajorMinor(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub
                    if(arg1==0){//写入成功
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                        builder.setTitle("提示"); //设置标题
                        builder.setMessage("配置成功"); //设置内容

                        //为了保证按钮的顺序一样，所以只能反过来设置
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        //参数都设置完成了，创建并显示出来
                        builder.create().show();
                    }else {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                        builder.setTitle("提示"); //设置标题
                        builder.setMessage("配置失败！"); //设置内容

                        //为了保证按钮的顺序一样，所以只能反过来设置
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        //参数都设置完成了，创建并显示出来
                        builder.create().show();
                    }

                }

                @Override
                public void onWritePassword(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onWriteProximityUUID(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onWriteSecureBroadcastInterval(Beacon arg0, int arg1) {
                    // TODO Auto-generated method stub

                }

            };



    public EditDeviceAdapter getEditDeviceAdapter() {
        return editDeviceAdapter;
    }

    public EditDeviceFrame getThisFrame() {
        return thisFrame;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device_frame);

        this.thisFrame = this;

        //自定义actionbar
        bar = getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_edit_device);
        saveBtn = (Button) bar.getCustomView().findViewById(R.id.saveEdtionBtn);

        TextView editDeviceTitle = (TextView) bar.getCustomView().findViewById(R.id.editDeviceTitle);
        editDeviceTitle.setText("编辑设备");


        Intent intent = getIntent();
        deviceIndexInList = intent.getIntExtra("deviceIndexInList", 0);
        deviceId = intent.getStringExtra("deviceId");
        uuid = intent.getStringExtra("uuid");
        deviceName = intent.getStringExtra("deviceName");
        major = intent.getStringExtra("major");
        minor = intent.getStringExtra("minor");
        token = intent.getStringExtra("token");
        relationalPagesIdBundle = intent.getBundleExtra("relationalPagesIdBundle");
        relationalPagesId = relationalPagesIdBundle.getStringArray("relationalPagesIdArray");

        deleteFrameHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DELETE_PAGE_SUCCESS:
                        Bundle bundle=msg.getData();
                        String deletedPageId=bundle.getString("deletedPageId");
                        int j=0;
                        for (int i=0;i<relationalPagesId.length;i++){
                            if(relationalPagesId[i]!=deletedPageId){
                                relationalPagesId[j]=relationalPagesId[i];
                                j++;
                            }else {
                                relationalPagesId[j]="";
                                j++;
                            }
                        }

                        hasEdittedReplyPage = true;
                        saveBtn.setEnabled(true);


                        break;
                }
            }
        };

        ArrayList<HashMap<String, Object>> editDeviceFrameText;
        editDeviceFrameText = getData();
        editDeviceAdapter = new EditDeviceAdapter(this, editDeviceFrameText, token, relationalPagesId,deleteFrameHandler);
        editDeviceList = (CancelParentListViewEvent) findViewById(R.id.editDevice);
        editDeviceList.setAdapter(editDeviceAdapter);

        //配置云子设备的相关初始化语句
        sensoroManager = SensoroManager.getInstance(thisFrame);
        sensoroManager.setCloudServiceEnable(false);
/**
 * 开启服务
 **/
        try {
            sensoroManager.startService();
        } catch (Exception e) {
            e.printStackTrace(); // 监听异常
        }

        beaconManagerListener = new BeaconManagerListener() {
            @Override
            public void onUpdateBeacon(final ArrayList<Beacon> beacon) {
                beacons=beacon;

            }

            public void onNewBeacon(Beacon beacon) {

            }

            public void onGoneBeacon(Beacon beacon) {
            }
        };
        sensoroManager.setBeaconManagerListener(beaconManagerListener);



        editDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //编辑设备名的响应
                    case 0:
                        Intent editDeviceRemarksNameIntent = new Intent();
                        editDeviceRemarksNameIntent.setClass(thisFrame, EditDeviceRemarksNameFrame.class);
                        String originalDeviceName = (String) getEditDeviceAdapter().getItemAtMap(0, "deviceRemarksName");
                        editDeviceRemarksNameIntent.putExtra("originalDeviceRemarksName", originalDeviceName);
                        thisFrame.startActivityForResult(editDeviceRemarksNameIntent, EDITNAME_REQUEST_CODE);
                        break;
                    //配置设备的响应
                    case 3:
                        Intent intent = new Intent();
                        intent.setClass(thisFrame, QRCodeCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        thisFrame.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                        break;
                    //添加回复页面的响应
                    case 4:
                        Intent addReplayPageIntent = new Intent();
                        addReplayPageIntent.setClass(thisFrame, AddReplyPageFrame.class);
                        addReplayPageIntent.putExtra("token", token);
                        addReplayPageIntent.putExtra("relationalPagesId", relationalPagesId);
                        thisFrame.startActivityForResult(addReplayPageIntent, ADDREPLYPAGE_REQUEST_CODE);
                        break;
                }
            }
        });

        //保存按钮的监听事件
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasEdittedDeviceName == true && hasEdittedReplyPage == false) {
                    RequestData editDeviceRequestData;

                    boolean bothEdit = false;

                    editDeviceRequestData = new RequestData(null, null, deviceId, major, null, null, minor, uuid, newDeviceName, null, null, null, null);

                    ConnectRequest editDeviceConnectrequest = new ConnectRequest(token, thisFrame, deviceIndexInList, bothEdit, 0);

                    editDeviceConnectrequest.RequestAPI(RequestKind.EditDevice, editDeviceRequestData);
                } else if (hasEdittedReplyPage == true && hasEdittedDeviceName == false) {
                    framesNumberOnThisDevice = getEditDeviceAdapter().getEditDeviceFrameListAdapter().getCount();

                    boolean bothEdit = false;

                    RequestData deployPagesOnOneDeviceRequestData = new RequestData(null, null, getDeviceId(), getMajor(), relationalPagesId, null,
                            getMinor(), getUuid(), null, null, null, null, null);
                    ConnectRequest deployPagesOnOneDeviceConnectrequest = new ConnectRequest(token, thisFrame, deviceIndexInList, framesNumberOnThisDevice, bothEdit);
                    deployPagesOnOneDeviceConnectrequest.RequestAPI(RequestKind.DeployPages, deployPagesOnOneDeviceRequestData);
                } else if (hasEdittedReplyPage == true && hasEdittedDeviceName == true) {
                    RequestData editDeviceRequestData;

                    boolean bothEdit = true;
                    framesNumberOnThisDevice = getEditDeviceAdapter().getEditDeviceFrameListAdapter().getCount();

                    editDeviceRequestData = new RequestData(null, null, deviceId, major, relationalPagesId, null, minor, uuid, newDeviceName, null, null, null, null);

                    ConnectRequest editDeviceConnectrequest = new ConnectRequest(token, thisFrame, deviceIndexInList, bothEdit, framesNumberOnThisDevice);

                    editDeviceConnectrequest.RequestAPI(RequestKind.EditDevice, editDeviceRequestData);
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");

                    String SNNumber = "";

                    if (result.startsWith("A0") && result.length() == 42) {
                        SNNumber = result.substring(3, 15);
                    } else if (result.length() == 39) {
                        SNNumber = result.substring(0, 12);
                    } else if (result.startsWith("http://k6.lc/") || result.startsWith("https://k6.lc/")) {
                        SNNumber = result.substring(13, 25);
                    }

                    if (SNNumber.equals("")) {
                        Toast.makeText(thisFrame, "非云子设备二维码，请重试!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(beacons==null){
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                        builder.setTitle("提示"); //设置标题
                        builder.setMessage("初始化失败，请过段时间再试！"); //设置内容

                        //为了保证按钮的顺序一样，所以只能反过来设置
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        //参数都设置完成了，创建并显示出来
                        builder.create().show();
                    }else {
                        if(beacons.size()==0){
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(thisFrame);  //先得到构造器
                            builder.setTitle("提示"); //设置标题
                            builder.setMessage("对不起，未搜索到附近的beacon设备，请重新配置！"); //设置内容

                            //为了保证按钮的顺序一样，所以只能反过来设置
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            //参数都设置完成了，创建并显示出来
                            builder.create().show();
                        }else {
                            int i;
                            for (i = 0; i < beacons.size(); i++) {

                                if (beacons.get(i).getSerialNumber().equals(SNNumber)) {

                                    try {
                                        sensorobeaconconnection = new SensoroBeaconConnection(thisFrame, beacons.get(i), beaconconnectioncallback);
                                        sensorobeaconconnection.connect();

                                    } catch (SensoroBeaconConnection.SensoroException e) {
                                        e.printStackTrace();
                                    }

                                    break;
                                }

                            }
                        }
                    }




                }
                break;
            case EDITNAME_REQUEST_CODE://从编辑设备名页面返回
                if (resultCode == EDITNAME_RESULT_CODE) {
                    Bundle bundle = data.getExtras();
                    newDeviceName = bundle.getString("deviceName");
                    getEditDeviceAdapter().setItem(0, "deviceRemarksName", newDeviceName);
                    if (!newDeviceName.equals(deviceName)) {//只有修改过后才重新设置
                        editDeviceList.setAdapter(getEditDeviceAdapter());
                        hasEdittedDeviceName = true;
                        saveBtn.setEnabled(true);
                    }
                }
                break;
            case ADDREPLYPAGE_REQUEST_CODE://从添加回复页面返回

                if (resultCode == ADDREPLYPAGE_RESULT_CODE) {

                    Bundle bundle = data.getExtras();
                    relationalPagesId = bundle.getStringArray("page_idsArray");

                    RequestData addRelationalPagesRequestData = new RequestData(null, null, null, null, relationalPagesId, "1", null, null, null, null, null, null, null);
                    ConnectRequest addRelationalPagesConnectrequest = new ConnectRequest(token, editDeviceAdapter.getEditDeviceFrameListAdapter(),
                            editDeviceAdapter.getFrameOnThisDeviceList(), getThisFrame());
                    addRelationalPagesConnectrequest.RequestAPI(RequestKind.SearchPageList, addRelationalPagesRequestData);
                    hasEdittedReplyPage = true;
                    saveBtn.setEnabled(true);

                }


        }
    }

    //自定义的list数据
    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deviceRemarksTitle", "设备备注名");
        map.put("deviceRemarksName", deviceName);
        map.put("deviceRemarksArrow", R.drawable.right_arrow);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("MajorMinorTitle", "Major");
        map.put("MajorMinorNumber", major);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("MajorMinorTitle", "Minor");
        map.put("MajorMinorNumber", minor);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("WithIconTitle", "配置设备");
        map.put("Icon", R.drawable.qrcode_img);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("WithIconTitle", "添加回复页面");
        map.put("Icon", R.drawable.add_reply_button);
        list.add(map);

        map = new HashMap<String, Object>();
        list.add(map);


        return list;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_device_frame, menu);
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
