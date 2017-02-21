package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.Timer;
import java.util.TimerTask;

public class AddOrEditPageLinkFrame extends Activity {

    private final static int EDIT_PAGELINK_RESULT_CODE = 8;
    private final static int QUICKLY_SET_PAGELINK_REQUEST_CODE = 10;
    private final static int QUICKLY_SET_PAGELINK_RESULT_CODE = 10;

    private TextView settingPageLinkTitle;
    private EditText settingPageLinkEditText;
    private Button quicklySetPageLinkBtn;

    private ActionBar bar;

    private AddOrEditPageLinkFrame thisAddOrEditPageLinkFrame;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page_link_frame);

        thisAddOrEditPageLinkFrame = this;

        Intent intent = getIntent();
        String settingTitleString = intent.getStringExtra("settingTitle");
        String settingEditString = intent.getStringExtra("settingEdit");
        String operationType = intent.getStringExtra("operationType");
        token = intent.getStringExtra("token");


        settingPageLinkTitle = (TextView) findViewById(R.id.settingPageLinkTitle);
        settingPageLinkTitle.setText(settingTitleString);

        settingPageLinkEditText = (EditText) findViewById(R.id.settingPageLinkEditText);
        settingPageLinkEditText.setText(settingEditString);

        //下面这段代码是用于跳转到这个页面后立即弹出软键盘
        settingPageLinkEditText.setFocusable(true);
        settingPageLinkEditText.setFocusableInTouchMode(true);
        settingPageLinkEditText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) settingPageLinkEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(settingPageLinkEditText, 0);
                           }

                       },
                500);


        quicklySetPageLinkBtn = (Button) findViewById(R.id.quicklySetPageLinkBtn);
        quicklySetPageLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(thisAddOrEditPageLinkFrame, QuicklyGetPageLinkFrame.class);
                intent.putExtra("token", token);
                thisAddOrEditPageLinkFrame.startActivityForResult(intent, QUICKLY_SET_PAGELINK_REQUEST_CODE);
            }
        });


        //自定义actionbar
        bar = getActionBar();
        bar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_edit_device_remarks_name);//跟之前设置备注名的style相同，所以不重复设置了
        Button saveEditionBtn = (Button) bar.getCustomView().findViewById(R.id.saveEditDeviceRemarksNameBtn);

        saveEditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edittedPageLinkString = settingPageLinkEditText.getText().toString();
                if (edittedPageLinkString.equals(""))
                    edittedPageLinkString = "未填写";
                Intent editPageNameIntent = new Intent();
                editPageNameIntent.putExtra("edittedPageLinkString", edittedPageLinkString);
                setResult(EDIT_PAGELINK_RESULT_CODE, editPageNameIntent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == QUICKLY_SET_PAGELINK_RESULT_CODE) {
            Bundle bundle = data.getExtras();
            String linkUrl = bundle.getString("pageLinkUrl");
            settingPageLinkEditText.setText(linkUrl);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_page_link_frame, menu);
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
