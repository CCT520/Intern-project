package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.Timer;
import java.util.TimerTask;

public class AddOrEditPageInfoFrame extends Activity {

    private final static int EDIT_PAGENAME_RESULT_CODE = 5;
    private final static int EDIT_MAINTITLE_RESULT_CODE = 6;
    private final static int EDIT_SUBTITLE_RESULT_CODE = 7;

    private ActionBar bar;

    private EditText settingEditText;
    private TextView settingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page_info_frame);

        Intent intent=getIntent();
        String settingTitleString=intent.getStringExtra("settingTitle");
        String settingEditString=intent.getStringExtra("settingEdit");

        settingTitle=(TextView)findViewById(R.id.settingTitle);
        settingTitle.setText(settingTitleString);

        settingEditText=(EditText)findViewById(R.id.settingEditText);
        settingEditText.setText(settingEditString);

        //下面这段代码是用于跳转到这个页面后立即弹出软键盘
        settingEditText.setFocusable(true);
        settingEditText.setFocusableInTouchMode(true);
        settingEditText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) settingEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(settingEditText, 0);
                           }

                       },
                500);


        //自定义actionbar
        bar=getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_edit_device_remarks_name);//跟之前设置备注名的style相同，所以不重复设置了
        Button saveEditionBtn=(Button)bar.getCustomView().findViewById(R.id.saveEditDeviceRemarksNameBtn);
        saveEditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String settingEditTextString=settingEditText.getText().toString();
                if(settingEditTextString.equals(""))
                    settingEditTextString="未填写";
                String settingTitleText=settingTitle.getText().toString();
                switch (settingTitleText){
                    case "页面名称":
                        Intent editPageNameIntent = new Intent();
                        editPageNameIntent.putExtra("edittedPageNameString", settingEditTextString);
                        setResult(EDIT_PAGENAME_RESULT_CODE,editPageNameIntent);
                        finish();
                        break;
                    case "主标题":
                        Intent editMainTitleIntent = new Intent();
                        editMainTitleIntent.putExtra("edittedMainTitleString", settingEditTextString);
                        setResult(EDIT_MAINTITLE_RESULT_CODE,editMainTitleIntent);
                        finish();
                        break;
                    case "副标题":
                        Intent editSubTitleIntent = new Intent();
                        editSubTitleIntent.putExtra("edittedSubTitleString", settingEditTextString);
                        setResult(EDIT_SUBTITLE_RESULT_CODE,editSubTitleIntent);
                        finish();
                        break;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_page_info_frame, menu);
        return true;
    }

}
