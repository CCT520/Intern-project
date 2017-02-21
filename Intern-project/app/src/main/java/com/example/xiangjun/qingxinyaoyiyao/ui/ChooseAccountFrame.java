package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ChooseAccountAsyncTask;
import com.example.xiangjun.qingxinyaoyiyao.function.GetOfficialAccountListAsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChooseAccountFrame extends Activity {

    private String accountName;
    private String token;
    private String response;
    private ChooseAccountFrame thisFrame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_frame);


        this.thisFrame=this;

        Intent intent = getIntent();
        accountName = intent.getStringExtra("accountName");
        token = intent.getStringExtra("token");
        response=intent.getStringExtra("response");
        List<Map<String, Object>> mData=new ArrayList<Map<String, Object>>();

        GetOfficialAccountListAsyncTask getOfficialAccountListAsyncTask=new GetOfficialAccountListAsyncTask(response, thisFrame,
                mData, token, accountName);
        getOfficialAccountListAsyncTask.execute();



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_account_frame, menu);
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
