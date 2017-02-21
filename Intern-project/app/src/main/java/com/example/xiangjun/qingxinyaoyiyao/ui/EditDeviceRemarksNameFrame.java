package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDeviceRemarksNameFrame extends Activity {

    private final static int EDITNAME_RESULT_CODE = 2;

    private List<Map<String, Object>> mData;

    private String originalDeviceRemarksName;

    private ActionBar bar;

    private EditDeviceRemarksNameFrame thisEditDeviceRemarksNameFrame;
    private EditDeviceRemarksNameAdapter editDeviceRemarksNameAdapter;

//    private EditText editDeviceRemarksNameText;

    public EditDeviceRemarksNameAdapter getEditDeviceRemarksNameAdapter() {
        return editDeviceRemarksNameAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device_remarks_name_frame);

        thisEditDeviceRemarksNameFrame=this;
        Intent intent=getIntent();
        originalDeviceRemarksName=intent.getStringExtra("originalDeviceRemarksName");

        //将自定义的列表内容放入list容器中
        mData = getData();
        //自定义的适配器
        editDeviceRemarksNameAdapter=new EditDeviceRemarksNameAdapter(this,mData);
        //定义一个listview并设置适配器
        ListView editDeviceRemarksNameList = (ListView) findViewById(R.id.editDeviceRemarksNameList);
        editDeviceRemarksNameList.setAdapter(editDeviceRemarksNameAdapter);
//        editDeviceRemarksNameText=(EditText)findViewById(R.id.editDeviceRemarksNameText);
        //自定义actionbar
        bar=getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_edit_device_remarks_name);
        Button saveEditDeviceRemarksNameBtn=(Button)bar.getCustomView().findViewById(R.id.saveEditDeviceRemarksNameBtn);
        saveEditDeviceRemarksNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDeviceRemarksName = getEditDeviceRemarksNameAdapter().getChangedText();

//                String newDeviceRemarksName=editDeviceRemarksNameText.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("deviceName", newDeviceRemarksName);
                setResult(EDITNAME_RESULT_CODE,intent);
                finish();

            }
        });


    }

    //自定义的list数据
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("editDeviceRemarksNameStatic", "设置备注名");
        list.add(map1);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("editDeviceRemarksNameText", originalDeviceRemarksName);
        list.add(map2);

        return list;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_device_remarks_name_frame, menu);
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


