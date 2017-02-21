package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeployBatchOfDeviceFrame extends Activity {

    private DeployBatchOfDeviceAdapter deployBatchOfDeviceAdapter;
    private CancelParentListViewEvent deployBatchOfDeviceList;
    private List<Map<String, Object>> deployBatchOfDeviceTextList;
    //private EditDeviceFrameListAdapter editDeviceFrameListAdapter;

    private String frameName;
    private String frameHint;
    private String frameId;
    private String token;
    private Bitmap frameImage;
    private byte[] frameImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_batch_of_devices_frame);
        setTitle("批量部署到设备");

        Intent intent=getIntent();
        frameName=intent.getStringExtra("frameName");
        frameHint=intent.getStringExtra("frameHint");
        frameId=intent.getStringExtra("frameId");

        frameImageBytes=intent.getByteArrayExtra("frameImage");
        token=intent.getStringExtra("token");

        frameImage= BitmapFactory.decodeByteArray(frameImageBytes, 0,
                frameImageBytes.length);

        ArrayList<HashMap<String, Object>> deployBatchOfDeviceText;
        deployBatchOfDeviceText = getData();
        deployBatchOfDeviceAdapter = new DeployBatchOfDeviceAdapter(this,deployBatchOfDeviceText,token,frameId);
        deployBatchOfDeviceList=(CancelParentListViewEvent)findViewById(R.id.deployBatchOfDeviceListStyle);
        deployBatchOfDeviceList.setDivider(null);
        deployBatchOfDeviceList.setAdapter(deployBatchOfDeviceAdapter);

    }

    //自定义的list数据
    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deployBatchOfDeviceFrameName", "主标题：" + frameName);
        map.put("deployBatchOfDeviceFrameHint", "副标题：" + frameHint);
        map.put("deployBatchOfDeviceFrameImage", frameImage);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("deployBatchOfDeviceStaticInfo", "选择部署方式");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("deployBatchOfDeviceSegmentContext", this);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("chooseAllBtn", "全选以下设备");
        map.put("startDeployBtn", "开始批量部署");
        list.add(map);

        map = new HashMap<String, Object>();

        list.add(map);



        return list;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deploy_batch_of_devices_frame, menu);
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
