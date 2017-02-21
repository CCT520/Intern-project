package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.QuicklyGetPageLinkItemAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuicklyGetPageLinkFrame extends Activity {

    private RefreshableListView quicklyGetPageLinkList;
    private QuicklyGetPageLinkListViewAdapter quicklyGetPageLinkListViewAdapter;
    private List<Map<String, Object>> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quickly_get_page_link_frame);


        Intent intent=getIntent();
        String token=intent.getStringExtra("token");

        list=new ArrayList<Map<String, Object>>();
        Map<String, Object> map=new HashMap<String, Object>();
        list.add(map);

        quicklyGetPageLinkList=(RefreshableListView)findViewById(R.id.quicklyGetPageLinkLv);
        quicklyGetPageLinkListViewAdapter=new QuicklyGetPageLinkListViewAdapter(list,this,token,quicklyGetPageLinkList);

        quicklyGetPageLinkList.setAdapter(null,null,null,null,quicklyGetPageLinkListViewAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quickly_get_page_link_frame, menu);
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
