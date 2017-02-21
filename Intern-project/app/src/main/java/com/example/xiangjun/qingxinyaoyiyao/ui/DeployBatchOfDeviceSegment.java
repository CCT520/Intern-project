package com.example.xiangjun.qingxinyaoyiyao.ui;


import android.content.Context;

import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.xiangjun.qingxinyaoyiyao.R.*;

public class DeployBatchOfDeviceSegment extends LinearLayout {
    private TextView addTv;
    private TextView coverTv;
    private static String nowSelected="addTv";
    private onSegmentViewClickListener listener;

    public DeployBatchOfDeviceSegment(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }


    public DeployBatchOfDeviceSegment(Context context) {
        super(context);
        init();
    }

    public String getNowSelected() {
        return nowSelected;
    }


    private void init() {
//		this.setLayoutParams(new LinearLayout.LayoutParams(dp2Px(getContext(), 60), LinearLayout.LayoutParams.WRAP_CONTENT));
        addTv = new TextView(getContext());
        coverTv = new TextView(getContext());
        addTv.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        coverTv.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        addTv.setText("新增");
        coverTv.setText("覆盖");
        //XmlResourceParser xrp = getResources().getXml(drawable.deploy_batch_of_device_segment_item_textcolor);


        addTv.setGravity(Gravity.CENTER);
        coverTv.setGravity(Gravity.CENTER);
        addTv.setPadding(3, 6, 3, 6);
        coverTv.setPadding(3, 6, 3, 6);
        setSegmentTextSize(16);
        addTv.setBackgroundResource(drawable.segment_item_left);
        coverTv.setBackgroundResource(drawable.segment_item_right);
        addTv.setSelected(true);
        this.removeAllViews();
        this.addView(addTv);
        this.addView(coverTv);
        this.invalidate();

        if(addTv.isSelected()==true)
            addTv.setTextColor(Color.WHITE);
        else
            addTv.setTextColor(Color.RED);

        if(coverTv.isSelected()==true)
            coverTv.setTextColor(Color.WHITE);
        else
            coverTv.setTextColor(Color.RED);

        addTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (addTv.isSelected()) {
                    return;
                }
                addTv.setSelected(true);
                addTv.setTextColor(Color.WHITE);
                coverTv.setSelected(false);
                coverTv.setTextColor(Color.RED);
                nowSelected="addTv";
                if (listener != null) {
                    listener.onSegmentViewClick(addTv, 0);
                }
            }
        });
        coverTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (coverTv.isSelected()) {
                    return;
                }
                coverTv.setSelected(true);
                coverTv.setTextColor(Color.WHITE);
                addTv.setSelected(false);
                addTv.setTextColor(Color.RED);
                nowSelected="coverTv";
                if (listener != null) {
                    listener.onSegmentViewClick(coverTv, 1);
                }
            }
        });
    }
    /**
     * 设置字体大小 单位dip
     * <p>2014年7月18日</p>
     * @param dp
     * @author RANDY.ZHANG
     */
    public void setSegmentTextSize(int dp) {
        addTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
        coverTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
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
            addTv.setText(text);
        }
        if (position == 1) {
            coverTv.setText(text);
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
