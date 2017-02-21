package com.example.xiangjun.qingxinyaoyiyao.ui;

/**
 * Created by xiangjun on 15/11/24.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

public class EditDeviceFrameListAdapter extends BaseAdapter {

    private final static int DELETE_PAGE_SUCCESS = 21;

    private List<Map<String, Object>> list;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    private float origin_x, later_x;

    private ListView frameOnThisDeviceList;

    private Handler deleteFrameHandler;

    private int selectedIndex = -1;

    public EditDeviceFrameListAdapter(List<Map<String, Object>> list, Context context,ListView frameOnThisDeviceList,Handler deleteFrameHandler) {
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.frameOnThisDeviceList=frameOnThisDeviceList;
        this.deleteFrameHandler=deleteFrameHandler;
    }

    //自定义holder来装其他组件
    public final class ViewHolder {
        public ImageView editDeviceFrameImage;
        public TextView editDeviceFrameName;
        public TextView editDeviceFrameHint;
        public Button deleteFrameBtn;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    public ListView getFrameOnThisDeviceList() {
        return frameOnThisDeviceList;
    }


    public void addItem(Map<String, Object> map) {
        list.add(map);
    }

    public void clearAllItem() {
        list.clear();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public void setItemAtMap(int position, String key, Object value) {
        Map<String, Object> map = list.get(position);
        map.remove(key);
        map.put(key, value);
    }

    public void deleteItemAtMap(int position) {
        list.remove(position);
        getFrameOnThisDeviceList().setAdapter(this);
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

            convertView = mInflater.inflate(R.layout.edit_device_framelist_item_style, null);
            holder.editDeviceFrameImage = (ImageView) convertView.findViewById(R.id.editDeviceFrameImage);
            holder.editDeviceFrameName = (TextView) convertView.findViewById(R.id.editDeviceFrameName);
            holder.editDeviceFrameHint = (TextView) convertView.findViewById(R.id.editDeviceFrameHint);
            holder.deleteFrameBtn = (Button) convertView.findViewById(R.id.deleteRelationalFrameBtn);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        //将之前自定义好的信息显示出来
        String editDeviceFrameImageType = (String) (String) list.get(position).get("editDeviceFrameImageType");
        if (editDeviceFrameImageType.equals("bitmap"))
            holder.editDeviceFrameImage.setImageBitmap((Bitmap) list.get(position).get("editDeviceFrameImage"));
        else if (editDeviceFrameImageType.equals("drawable"))
            holder.editDeviceFrameImage.setBackgroundResource((Integer) list.get(position).get("editDeviceFrameImage"));

        holder.editDeviceFrameName.setText((String) list.get(position).get("editDeviceFrameName"));
        holder.editDeviceFrameHint.setText((String) list.get(position).get("editDeviceFrameHint"));
        list.get(position).put("deleteFrameBtn", holder.deleteFrameBtn);

        holder.deleteFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);  //先得到构造器
                builder.setTitle("提示"); //设置标题
                builder.setMessage("确定删除该页面么?"); //设置内容

                //为了保证按钮的顺序一样，所以只能反过来设置
                builder.setPositiveButton("容我三思", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        HashMap<String,Object> selectedMap=(HashMap)list.get(position);
                        String deletedPageId=(String)selectedMap.get("editDeviceFrameId");

                        Message message=deleteFrameHandler.obtainMessage();
                        message.what=DELETE_PAGE_SUCCESS;

                        Bundle bundle=new Bundle();
                        bundle.putString("deletedPageId",deletedPageId);

                        message.setData(bundle);

                        deleteFrameHandler.sendMessage(message);
                        deleteItemAtMap(position);

                    }
                });
                //参数都设置完成了，创建并显示出来
                builder.create().show();
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
                        for (int i = 0; i < getCount(); i++) {
                            if (i == selectedIndex) {
                                Button deleteFrameBtn = (Button) list.get(i).get("deleteFrameBtn");
                                deleteFrameBtn.setVisibility(View.GONE);
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP://松开
                        later_x = event.getX();
                        if (later_x - origin_x < -2) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            holder.deleteFrameBtn.setVisibility(View.VISIBLE);
                            selectedIndex = position;
                        } else if (later_x - origin_x > 2) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            holder.deleteFrameBtn.setVisibility(View.GONE);
                            selectedIndex = -1;
                        }
                        break;
                    default:
                }
                return true;
            }
        });


        return convertView;

    }

}

