package com.example.xiangjun.qingxinyaoyiyao.ui;

/**
 * Created by xiangjun on 15/11/24.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

public class MydeviceListAdapter extends BaseAdapter {

    private final static int EDITDEVICE_REQUEST_CODE = 3;

    private List<Map<String, Object>> list;
    private MainFrame thisMainFrame;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;
    private float origin_x, later_x;
    private float origin_y, later_y;
    private String token;
    private int lastLoadingMaxIndex = 0;
    private boolean hasFinishedLoading = false;
    private int selectedIndex = -1;

    public MydeviceListAdapter(List<Map<String, Object>> list, MainFrame thisMainFrame, String token) {
        this.list = list;
        this.thisMainFrame = thisMainFrame;
        this.mInflater = LayoutInflater.from(thisMainFrame);
        this.token = token;
    }

    public List<Map<String, Object>> getList() {
        return this.list;
    }

    public int getLastLoadingMaxIndex() {
        return lastLoadingMaxIndex;
    }

    public void setLastLoadingMaxIndex(int lastLoadingMaxIndex) {
        this.lastLoadingMaxIndex = lastLoadingMaxIndex;
    }

    public boolean isHasFinishedLoading() {
        return hasFinishedLoading;
    }

    public void setHasFinishedLoading(boolean hasFinishedLoading) {
        this.hasFinishedLoading = hasFinishedLoading;
    }

    //自定义holder来装按钮与其他组件
    public final class ViewHolder {
        public ImageView isActiviatedImage;
        public TextView deviceName;
        public TextView deviceHint;
        public Button dataBtn;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void addItem(Map<String, Object> map) {
        list.add(map);
    }

    public void addItemAtMap(int index, String key, String value) {
        Map<String, Object> map = list.get(index);
        map.put(key, value);
    }

    public void addItemsAtMap(int index, String key, String[] value) {
        Map<String, Object> map = list.get(index);
        map.put(key, value);
    }

    public void setItem(int index, String key, String value) {
        Map<String, Object> map = list.get(index);
        map.remove(key);
        map.put(key, value);
    }

    public void setItemsAtMap(int index, String key, String[] value) {
        Map<String, Object> map = list.get(index);
        map.remove(key);
        map.put(key, value);
    }


    public void deleteAllItems() {
        list.clear();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            //初始化holder的各个变量
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.mydevice_list_item, null);
            holder.isActiviatedImage = (ImageView) convertView.findViewById(R.id.isActiviatedImage);
            holder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            holder.deviceHint = (TextView) convertView.findViewById(R.id.deviceHint);
            holder.dataBtn = (Button) convertView.findViewById(R.id.dataBtn);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

//        if (position != selectedIndex)
//            holder.dataBtn.setVisibility(View.GONE);
//        else
//            holder.dataBtn.setVisibility(View.VISIBLE);

        //将之前自定义好的信息显示出来
        holder.isActiviatedImage.setBackgroundResource((Integer) list.get(position).get("isActiviatedImage"));
        holder.deviceName.setText((String) list.get(position).get("deviceName"));
        holder.deviceHint.setText((String) list.get(position).get("deviceHint"));
        list.get(position).put("dataBtn", holder.dataBtn);

        holder.dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < getCount(); i++) {
                    if (i == selectedIndex) {
                        Button dataBtn = (Button) list.get(i).get("dataBtn");
                        dataBtn.setVisibility(View.GONE);
                        break;
                    }
                }


                Intent intent = new Intent();

                HashMap<String, Object> selectedMap = (HashMap<String, Object>) list.get(position);

                String device_id = (String) selectedMap.get("device_id");
                String major = (String) selectedMap.get("major");
                String minor = (String) selectedMap.get("minor");
                String uuid = (String) selectedMap.get("uuid");
                String token = (String) selectedMap.get("token");
                intent.putExtra("deviceId", device_id);
                intent.putExtra("major", major);
                intent.putExtra("minor", minor);
                intent.putExtra("uuid", uuid);
                intent.putExtra("token", token);
                intent.setClass(thisMainFrame, DeviceDataFrame.class);
                thisMainFrame.startActivity(intent);
            }
        });


        convertView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())//根据动作来执行代码
                {
                    case MotionEvent.ACTION_MOVE://滑动
                        break;
                    case MotionEvent.ACTION_DOWN://按下
                        origin_x = event.getX();
                        origin_y = event.getY();
                        for (int i = 0; i < getCount(); i++) {
                            if (i == selectedIndex) {
                                Button dataBtn = (Button) list.get(i).get("dataBtn");
                                dataBtn.setVisibility(View.GONE);
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP://松开
                        later_x = event.getX();
                        later_y = event.getY();
                        if (later_x - origin_x < -2) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            holder.dataBtn.setVisibility(View.VISIBLE);
                            selectedIndex = position;
                        } else if (later_x - origin_x > 2) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            holder.dataBtn.setVisibility(View.GONE);
                            selectedIndex = -1;
                        } else if (later_x == origin_x) {
                            onItemClickListener(position);
                        }
                        break;
                    default:
                }
                return true;
            }
        });


        return convertView;

    }


    public void onItemClickListener(int position) {
        HashMap<String, Object> selectedItemMap =
                (HashMap) getItem(position);
        String deviceName = (String) selectedItemMap.get("deviceName");
        String major = (String) selectedItemMap.get("major");
        String minor = (String) selectedItemMap.get("minor");
        String deviceId = (String) selectedItemMap.get("device_id");
        String uuid = (String) selectedItemMap.get("uuid");

        String[] relationalPagesId = (String[]) selectedItemMap.get("relationalPagesIdArray");
        Bundle relationalPagesIdBundle = new Bundle();
        relationalPagesIdBundle.putStringArray("relationalPagesIdArray", relationalPagesId);

        Intent intent = new Intent();
        intent.putExtra("deviceIndexInList", position);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("uuid", uuid);
        intent.putExtra("major", major);
        intent.putExtra("minor", minor);
        intent.putExtra("token", token);
        intent.putExtra("relationalPagesIdBundle", relationalPagesIdBundle);
        intent.setClass(thisMainFrame, EditDeviceFrame.class);
        thisMainFrame.startActivityForResult(intent, EDITDEVICE_REQUEST_CODE);
    }

}

