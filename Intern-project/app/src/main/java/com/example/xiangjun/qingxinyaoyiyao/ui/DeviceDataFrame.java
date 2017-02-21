package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.GetDeviceDataAsyncTask;
import com.github.mikephil.charting.charts.LineChart;

public class DeviceDataFrame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data_frame);

        setTitle("设备数据");

        Intent intent=getIntent();
        String device_id=intent.getStringExtra("deviceId");
        String major=intent.getStringExtra("major");
        String minor=intent.getStringExtra("minor");
        String uuid=intent.getStringExtra("uuid");
        String token=intent.getStringExtra("token");

        String URL="http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=device_statistics&token="+token;

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("正在加载中，请稍候...");
        builder.setCancelable(false);
        AlertDialog loadingDataDialog=builder.create();
        loadingDataDialog.show();

        ListView list=(ListView)findViewById(R.id.statisticslist);
        LineChart mLineChart = (LineChart) findViewById(R.id.chart1);
        GetDeviceDataAsyncTask m = new GetDeviceDataAsyncTask(device_id,major,minor,uuid,mLineChart,list,this,loadingDataDialog);
        m.execute(URL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_data_frame, menu);
        return true;
    }

}
