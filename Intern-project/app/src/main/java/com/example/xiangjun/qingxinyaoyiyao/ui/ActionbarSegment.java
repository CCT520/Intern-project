package com.example.xiangjun.qingxinyaoyiyao.ui;


import android.content.Context;

import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.xiangjun.qingxinyaoyiyao.R.*;

public class ActionbarSegment extends LinearLayout {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private onSegmentViewClickListener listener;

    private RefreshableListView mydevicelv;
    private MydeviceListAdapter mydeviceListAdapter;
    private MainFrame thisMainFrame;

    public ActionbarSegment(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }


    public ActionbarSegment(Context context) {
        super(context);
        init();
    }

    public void setMydevicelv(RefreshableListView mydevicelv) {
        this.mydevicelv = mydevicelv;
    }

    public void setMydeviceListAdapter(MydeviceListAdapter mydeviceListAdapter) {
        this.mydeviceListAdapter = mydeviceListAdapter;
    }

    public void setThisMainFrame(MainFrame thisMainFrame) {
        this.thisMainFrame = thisMainFrame;
    }

    public RefreshableListView getMydevicelv() {
        return mydevicelv;
    }

    public MydeviceListAdapter getMydeviceListAdapter() {
        return mydeviceListAdapter;
    }

    public MainFrame getThisMainFrame() {
        return thisMainFrame;
    }

    private void init() {
        textView1 = new TextView(getContext());
        textView2 = new TextView(getContext());
        textView3 = new TextView(getContext());
        textView1.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        textView2.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        textView3.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        textView1.setText("全部");
        textView2.setText("已激活");
        textView3.setText("未激活");


        textView1.setGravity(Gravity.CENTER);
        textView2.setGravity(Gravity.CENTER);
        textView3.setGravity(Gravity.CENTER);
        textView1.setPadding(30, 15, 40, 15);
        textView2.setPadding(30, 15, 30, 15);
        textView3.setPadding(30, 15, 30, 15);
        setSegmentTextSize(16);
        textView1.setBackgroundResource(drawable.segment_item_left);
        textView2.setBackgroundResource(drawable.segment_item_middle);
        textView3.setBackgroundResource(drawable.segment_item_right);



        textView1.setSelected(true);
        this.removeAllViews();
        this.addView(textView1);
        this.addView(textView2);
        this.addView(textView3);
        this.invalidate();

        if(textView1.isSelected()==true)
            textView1.setTextColor(Color.WHITE);
        else
            textView1.setTextColor(Color.RED);

        if(textView2.isSelected()==true)
            textView2.setTextColor(Color.WHITE);
        else
            textView2.setTextColor(Color.RED);

        if(textView3.isSelected()==true)
            textView3.setTextColor(Color.WHITE);
        else
            textView3.setTextColor(Color.RED);

        textView1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView1.isSelected()) {
                    return;
                }
                textView1.setSelected(true);
                textView1.setTextColor(Color.WHITE);
                textView2.setSelected(false);
                textView2.setTextColor(Color.RED);
                textView3.setSelected(false);
                textView3.setTextColor(Color.RED);
                getMydevicelv().setAdapter(getMydeviceListAdapter(), null, null,null,null);
            }
        });
        textView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView2.isSelected()) {
                    return;
                }
                textView2.setSelected(true);
                textView2.setTextColor(Color.WHITE);
                textView1.setSelected(false);
                textView1.setTextColor(Color.RED);
                textView3.setSelected(false);
                textView3.setTextColor(Color.RED);

                List<Map<String, Object>> activiatedList=new ArrayList<Map<String, Object>>();;
                for(int i=0;i<getMydeviceListAdapter().getList().size();i++){
                    Map<String, Object> map=getMydeviceListAdapter().getList().get(i);
                    boolean isActiviated=(boolean)map.get("isActiciated");
                    if(isActiviated==true)
                        activiatedList.add(map);
                }
                String token=(String)getMydeviceListAdapter().getList().get(0).get("token");
                MydeviceListAdapter activiatedMydeviceListAdapter=new MydeviceListAdapter(activiatedList,getThisMainFrame(),token);
                getMydevicelv().setAdapter(activiatedMydeviceListAdapter, null, null,null,null);
            }
        });
        textView3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView3.isSelected()) {
                    return;
                }
                textView3.setSelected(true);
                textView3.setTextColor(Color.WHITE);
                textView1.setSelected(false);
                textView1.setTextColor(Color.RED);
                textView2.setSelected(false);
                textView2.setTextColor(Color.RED);

                List<Map<String, Object>> isNotActiviatedList=new ArrayList<Map<String, Object>>();;
                for(int i=0;i<getMydeviceListAdapter().getList().size();i++){
                    Map<String, Object> map=getMydeviceListAdapter().getList().get(i);
                    boolean isActiviated=(boolean)map.get("isActiciated");
                    if(isActiviated==false)
                        isNotActiviatedList.add(map);
                }
                String token=(String)getMydeviceListAdapter().getList().get(0).get("token");
                MydeviceListAdapter isNotMydeviceListAdapter=new MydeviceListAdapter(isNotActiviatedList,getThisMainFrame(),token);
                getMydevicelv().setAdapter(isNotMydeviceListAdapter, null, null,null,null);
            }
        });
    }
    /**
     * 设置字体大小 单位sp
     * <p>2014年7月18日</p>
     * @param sp
     * @author RANDY.ZHANG
     */
    public void setSegmentTextSize(int sp) {
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    private static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setOnSegmentViewClickListener(onSegmentViewClickListener listener) {
        this.listener = listener;
    }


    /**
     * 设置文字
     * <p>2014年7月18日</p>
     * @param text
     * @param position
     */
    public void setSegmentText(CharSequence text,int position) {
        if (position == 0) {
            textView1.setText(text);
        }
        if (position == 1) {
            textView2.setText(text);
        }
        if (position == 2) {
            textView3.setText(text);
        }
    }

    public static interface onSegmentViewClickListener{
        /**
         *
         * <p>2014年7月18日</p>
         * @param v
         * @param position 0-左边 1-右边
         */
        public void onSegmentViewClick(View v,int position);
    }
}
