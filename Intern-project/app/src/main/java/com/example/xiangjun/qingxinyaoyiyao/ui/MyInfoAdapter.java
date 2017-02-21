package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangjun on 15/11/24.
 */
public class MyInfoAdapter extends BaseAdapter {

    ArrayList<HashMap<String, Object>> ls;
    MainFrame thisMainFrame;
    LinearLayout linearLayout = null;
    LayoutInflater inflater;
    TextView tex;
    final int VIEW_TYPE = 5;
    final int TYPE_1 = 0;//静态文本
    final int TYPE_2 = 1;//账号号码
    final int TYPE_3 = 2;//账户信息
    final int TYPE_4 = 3;//退出按钮
    final int TYPE_5 = 4;//公众号信息

    ViewHolder1 holder1 = null;
    ViewHolder2 holder2 = null;
    ViewHolder3 holder3 = null;
    ViewHolder4 holder4 = null;
    ViewHolder5 holder5 = null;


    public MyInfoAdapter(MainFrame thisMainFrame,
                          ArrayList<HashMap<String, Object>> list) {
        ls = list;
        this.thisMainFrame = thisMainFrame;
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    public void setImage(Bitmap value){
        Map<String, Object> map=ls.get(1);
        map.remove("chosenOfficialAccountImage");
        map.put("chosenOfficialAccountImage",value);
    }

    public void setItem(int index,String key,String value){
        Map<String, Object> map=ls.get(index);
        map.remove(key);
        map.put(key,value);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0 || p==2 || p==4)//静态文本
            return TYPE_1;
        else if (p == 3)//账号号码
            return TYPE_2;
        else if(p==5 ||p==6)//账户信息
            return TYPE_3;
        else if(p==7)//退出按钮
            return TYPE_4;
        else if(p==1)//公众号信息
            return TYPE_5;
        else
            return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        if (convertView == null) {
            inflater = LayoutInflater.from(thisMainFrame);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.my_info_item_style1,
                            parent, false);
                    holder1 = new ViewHolder1();
                    holder1.staticInfo = (TextView) convertView
                            .findViewById(R.id.staticInfoStyle);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.my_info_item_style2,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.accountName = (TextView) convertView
                            .findViewById(R.id.accountNameStyle);
                    convertView.setTag(holder2);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.my_info_item_style3,
                            parent, false);
                    holder3 = new ViewHolder3();
                    holder3.accountInfoTitle = (TextView) convertView
                            .findViewById(R.id.accountBalanceTitleStyle);
                    holder3.accountInfo = (TextView) convertView
                            .findViewById(R.id.accountBalanceNumberStyle);
                    convertView.setTag(holder3);
                    break;
                case TYPE_4:
                    convertView = inflater.inflate(R.layout.my_info_item_style4,
                            parent, false);
                    holder4 = new ViewHolder4();
                    holder4.exitBtn = (Button) convertView
                            .findViewById(R.id.exitBtnStyle);
                    convertView.setTag(holder4);
                    break;
                case TYPE_5:
                    convertView = inflater.inflate(R.layout.my_info_item_style5,
                            parent, false);
                    holder5 = new ViewHolder5();
                    holder5.chosenOfficialAccountImage = (ImageView) convertView
                            .findViewById(R.id.chosenOfficialAccountImage);
                    holder5.chosenOfficialAccountName = (TextView) convertView
                            .findViewById(R.id.chosenOfficialAccountName);
                    holder5.chosenOfficialAccountHint = (TextView) convertView
                            .findViewById(R.id.chosenOfficialAccountHint);
                    holder5.affiliatedCareer = (TextView) convertView
                            .findViewById(R.id.affliatedCareer);
                    convertView.setTag(holder5);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
                case TYPE_3:
                    holder3 = (ViewHolder3) convertView.getTag();
                    break;
                case TYPE_4:
                    holder4 = (ViewHolder4) convertView.getTag();
                    break;
                case TYPE_5:
                    holder5 = (ViewHolder5) convertView.getTag();
                    break;
            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1://静态信息
                switch (position){
                    case 0:holder1.staticInfo.setText((String)ls.get(position).get("staticInfo1"));
                        break;
                    case 2:holder1.staticInfo.setText((String)ls.get(position).get("staticInfo2"));
                        break;
                    case 4:holder1.staticInfo.setText((String)ls.get(position).get("staticInfo3"));
                        break;
                }
                break;
            case TYPE_2://用户账号(position=3)
                holder2.accountName.setText((String)ls.get(position).get("accountName"));
                break;
            case TYPE_3://用户消费情况
                switch (position) {
                    case 5:holder3.accountInfoTitle.setText((String)ls.get(position).get("ValidDateTitle"));
                        holder3.accountInfo.setText((String) ls.get(position).get("ValidDate"));
                        break;
                    case 6:holder3.accountInfoTitle.setText((String)ls.get(position).get("myFinanceRemainTitle"));
                        holder3.accountInfo.setText((String) ls.get(position).get("myFinanceRemain"));
                        break;
                }
                break;
            case TYPE_4://退出登录按钮(position=8)
                holder4.exitBtn.setText((String) ls.get(position).get("exitBtnText"));
                holder4.exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder=new AlertDialog.Builder(thisMainFrame);  //先得到构造器
                        builder.setTitle("提示"); //设置标题
                        builder.setMessage("真的要退出么?"); //设置内容

                        //为了保证按钮的顺序一样，所以只能反过来设置
                        builder.setPositiveButton("容朕三思", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("再见了！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences userInfoSharedPreferences = thisMainFrame.getSharedPreferences("userInfoCache",
                                        Activity.MODE_PRIVATE);
                                SharedPreferences pageNumberSharedPreferences = thisMainFrame.getSharedPreferences("pageNumberCache",
                                        Activity.MODE_PRIVATE);

                                userInfoSharedPreferences.edit().clear().commit();
                                pageNumberSharedPreferences.edit().clear().commit();

                                Intent intent = new Intent();
                                intent.putExtra("from", "exitBtn");
                                intent.putExtra("loggedinName", holder2.accountName.getText().toString());
                                intent.setClass(thisMainFrame, LoginFrame.class);
                                thisMainFrame.startActivity(intent);
                                thisMainFrame.finish();
                            }
                        });

                        //参数都设置完成了，创建并显示出来
                        builder.create().show();

                    }
                });
                break;
            case TYPE_5://公众号信息
                holder5.chosenOfficialAccountImage.setImageBitmap((Bitmap) ls.get(position).get("chosenOfficialAccountImage"));
                holder5.chosenOfficialAccountName.setText((String) ls.get(position).get("chosenOfficialAccountName"));
                holder5.chosenOfficialAccountHint.setText((String) ls.get(position).get("chosenOfficialAccountHint"));
                holder5.affiliatedCareer.setText((String) ls.get(position).get("affliatedCareer"));
                break;

        }

        return convertView;
    }

    public class ViewHolder1 {
        TextView staticInfo;
    }

    public class ViewHolder2 {
        TextView accountName;
    }

    public class ViewHolder3 {
        TextView accountInfoTitle;
        TextView accountInfo;
    }

    public class ViewHolder4 {
        Button exitBtn;
    }

    public class ViewHolder5 {//公众号信息的holder
        public ImageView chosenOfficialAccountImage;
        public TextView chosenOfficialAccountName;
        public TextView chosenOfficialAccountHint;
        public TextView affiliatedCareer;
    }


}
