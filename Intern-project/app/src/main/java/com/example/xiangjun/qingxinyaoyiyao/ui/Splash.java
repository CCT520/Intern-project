package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;


public class Splash extends Activity implements Serializable{
    private final int SPLASH_DISPLAY_LENGHT = 3000; //延迟三秒

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {

                Date date = new Date();
                //实例化SharedPreferences对象（第一步）
                SharedPreferences userInfoSharedPreferences = getSharedPreferences("userInfoCache",
                        Activity.MODE_PRIVATE);
                // 使用getString方法获得value，注意第2个参数是value的默认值
                Boolean isCached = userInfoSharedPreferences.getBoolean("isCached", false);

                if (isCached == false) {//如果没缓存

                    Intent mainIntent = new Intent();
                    mainIntent.putExtra("from","splash");
                    mainIntent.setClass(Splash.this, LoginFrame.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                } else {//如果已缓存,则判断有没有过期
                    String cachedDateString = userInfoSharedPreferences.getString("cachedDate", "");
                    long cacheDate = Long.parseLong(cachedDateString);
                    long todayDate = date.getTime();
                    int diffDays=(int)(todayDate-cacheDate)/(1000*60*60*24);//计算时间戳是否过期

                    if (diffDays >= 5) {//若已过期，则把缓存删除
                        SharedPreferences pageNumberSharedPreferences = getSharedPreferences("pageNumberCache",
                                Activity.MODE_PRIVATE);
                        userInfoSharedPreferences.edit().clear().commit();
                        pageNumberSharedPreferences.edit().clear().commit();
                        Intent mainIntent = new Intent();
                        mainIntent.putExtra("from","splash");
                        mainIntent.setClass(Splash.this, LoginFrame.class);
                        Splash.this.startActivity(mainIntent);
                        Splash.this.finish();
                    } else {//如果未过期

                        String chosenOfficialAccount = userInfoSharedPreferences.getString("chosenOfficialAccountName", "");
                        String chosenOfficialAccountHint = userInfoSharedPreferences.getString("chosenOfficialAccountHint", "");
                        String accountName = userInfoSharedPreferences.getString("accountName", "");
                        String chosenOfficialAccountAffliatedCareer = userInfoSharedPreferences.getString("chosenOfficialAccountAffliatedCareer", "");
                        String chosenOfficialAccountHeadImageURl = userInfoSharedPreferences.getString("chosenOfficialAccountHeadImageURl", "");
                        String token = userInfoSharedPreferences.getString("token", "");

                        Intent intent = new Intent();
                        intent.putExtra("chosenOfficialAccountName", chosenOfficialAccount);
                        intent.putExtra("chosenOfficialAccountHint", chosenOfficialAccountHint);
                        intent.putExtra("accountName", accountName);
                        intent.putExtra("chosenOfficialAccountAffliatedCareer", chosenOfficialAccountAffliatedCareer);
                        intent.putExtra("imageIsURL", true);
                        intent.putExtra("chosenOfficialAccountHeadImageURl", chosenOfficialAccountHeadImageURl);
                        intent.putExtra("token", token);
                        intent.setClass(Splash.this, MainFrame.class);
                        Splash.this.startActivity(intent);
                        Splash.this.finish();
                    }
                }
            }

        }, SPLASH_DISPLAY_LENGHT);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
