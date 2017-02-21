package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;

public class AddReplyPageFrame extends Activity {

    private ActionBar bar;

    private RefreshableListView addReplyPagelv;
    private String[] relationalPagesId;
    private Button chooseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reply_page_frame);

        Intent intent=getIntent();
        String token=intent.getStringExtra("token");
        relationalPagesId=intent.getStringArrayExtra("relationalPagesId");

        //自定义actionbar
        bar=getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_add_reply_page);

        TextView addReplayPageBarTitle=(TextView)bar.getCustomView().findViewById(R.id.addReplyPageTitle);
        addReplayPageBarTitle.setText("选择页面");

        chooseBtn=(Button)bar.getCustomView().findViewById(R.id.chooseBtn);

        addReplyPagelv=(RefreshableListView)findViewById(R.id.addReplyPagelv);

        //获取页面列表以显示
        RequestData addReplyPageRequestData=new RequestData("0","20",null,null,null,"2",null,null,null,null,null,null,null);
        ConnectRequest addReplyPageConnectrequest = new ConnectRequest(token,null,addReplyPagelv,this,relationalPagesId,chooseBtn);

        addReplyPageConnectrequest.RequestAPI(RequestKind.SearchPageList, addReplyPageRequestData);

    }


}
