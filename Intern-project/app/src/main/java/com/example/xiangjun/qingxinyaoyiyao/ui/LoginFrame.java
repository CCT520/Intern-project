package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.function.*;

import com.example.xiangjun.qingxinyaoyiyao.R;


public class LoginFrame extends Activity {


    private TextView loginName, loginPwd;
    private ScrollView scroll;
    private LoginAsyncTask loginAsyncTask;
    private LoginFrame thisFrame=this;
    private Splash thisSplashFrame;

    private AlertDialog.Builder buider;
    private AlertDialog isLogginginDialog;

    public LoginFrame getThisFrame() {
        return thisFrame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_frame);


        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginName = (TextView) findViewById(R.id.loginName_text);
        loginPwd = (TextView) findViewById(R.id.loginPwd_text);
        scroll = (ScrollView) findViewById(R.id.loginScroll);

        SharedPreferences loginNameCachePreferences= thisFrame.getSharedPreferences("loginNameCache",
                Activity.MODE_PRIVATE);
        Boolean loginNameIsCached = loginNameCachePreferences.getBoolean("loginNameIsCached", false);

        if(loginNameIsCached==true){
            String latestLoginName=loginNameCachePreferences.getString("latestLoginName","");
            if(!latestLoginName.equals("")){
                loginName.setText(latestLoginName);
            }
        }


        Intent intent=getIntent();
        String from=intent.getStringExtra("from");
        if(from.equals("exitBtn")){
            String loggedinName=intent.getStringExtra("loggedinName");
            loginName.setText(loggedinName);
        }


        loginName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {

                        int[] location = new int[2];
                        loginName.getLocationOnScreen(location);
                        int offset = scroll.getMeasuredHeight() - location[1];

                        if (offset < 0) {
                            offset = 0;
                        }
                        if (scroll.getScrollY() == 0)
                            scroll.smoothScrollTo(0, offset);
                        else {
                            offset = scroll.getMeasuredHeight() - scroll.getScrollY() - (location[1] - scroll.getScrollY());
                            scroll.smoothScrollTo(scroll.getScrollY(), offset);
                        }
                    }

                });
            }
        });

        loginPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {

                        int[] location = new int[2];
                        loginPwd.getLocationOnScreen(location);
                        int offset = scroll.getMeasuredHeight() - location[1];

                        if (offset < 0) {
                            offset = 0;
                        }
                        if (scroll.getScrollY() == 0)
                            scroll.smoothScrollTo(0, offset);
                        else {
                            offset = scroll.getMeasuredHeight() - scroll.getScrollY() - (location[1] - scroll.getScrollY());
                            scroll.smoothScrollTo(scroll.getScrollY(), offset);
                        }
                    }

                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //检查网络连接
                ConnectivityManager cm = (ConnectivityManager) getThisFrame().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info == null || !info.isConnected()) {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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

                String loginNameText = loginName.getText().toString();
                String loginPwdText=loginPwd.getText().toString();

                if (loginNameText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if (loginPwdText.isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                buider=new AlertDialog.Builder(thisFrame);
                buider.setMessage("正在登录，请稍候...");
                buider.setCancelable(false);
                isLogginginDialog=buider.create();
                isLogginginDialog.show();

                loginAsyncTask=new LoginAsyncTask(loginNameText,loginPwdText,thisFrame,isLogginginDialog);
                loginAsyncTask.execute();



            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

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
