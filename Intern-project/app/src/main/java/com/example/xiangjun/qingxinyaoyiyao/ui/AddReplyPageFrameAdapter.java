package com.example.xiangjun.qingxinyaoyiyao.ui;

/**
 * Created by xiangjun on 15/11/24.
 */
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;

public class AddReplyPageFrameAdapter extends BaseAdapter {


    private List<Map<String, Object>> list;
    private AddReplyPageFrame thisAddReplayPageFrame;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    private int lastLoadingMaxIndex=0;
    private boolean hasFinishedLoading=false;

    private ArrayList<Integer> selectedIndexList = new ArrayList<Integer>();

    public AddReplyPageFrameAdapter(List<Map<String, Object>> list,AddReplyPageFrame thisAddReplayPageFrame) {
        this.list = list;
        this.thisAddReplayPageFrame = thisAddReplayPageFrame;
        this.mInflater = LayoutInflater.from(this.thisAddReplayPageFrame);
    }


    //自定义holder来装其他组件
    public final class ViewHolder{
        public ImageView replayPageFrameImage;
        public TextView replayPageFrameName;
        public TextView replayPageFrameHint;
        public ImageView addReplyPageChooseFlag;
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

    public void addInSelectedList(int selectedIndex) {
        selectedIndexList.add(selectedIndex);
    }

    public void deleteSelectedIndex(int toDeleteIndex) {
        for (int i = 0; i < selectedIndexList.size(); i++) {
            if (selectedIndexList.get(i) == toDeleteIndex) {
                selectedIndexList.remove(i);
                break;
            }
        }
    }

    public boolean isInSelectedList(int index) {
        for (int i = 0; i < selectedIndexList.size(); i++) {
            if (selectedIndexList.get(i) == index)
                return true;
        }
        return false;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    public void addItem(Map<String, Object> map){
        list.add(map);
    }


    public void setItemAtMap(int position,String key,Object value){
        Map<String, Object> map = list.get(position);
        map.remove(key);
        map.put(key, value);
    }

    public void deleteItem(int position){
        list.remove(position);
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
            holder=new ViewHolder();

            convertView = mInflater.inflate(R.layout.add_reply_page_list_item, null);
            holder.replayPageFrameImage = (ImageView)convertView.findViewById(R.id.replayPageFrameImage);
            holder.replayPageFrameName = (TextView)convertView.findViewById(R.id.replayPageFrameName);
            holder.replayPageFrameHint = (TextView)convertView.findViewById(R.id.replayPageFrameHint);
            holder.addReplyPageChooseFlag=(ImageView)convertView.findViewById(R.id.addReplyPageChooseFlag);


            convertView.setTag(holder);

        }else {

            holder = (ViewHolder)convertView.getTag();
        }

        if (isInSelectedList(position)) {
            holder.addReplyPageChooseFlag.setVisibility(View.VISIBLE);
        }else {
            holder.addReplyPageChooseFlag.setVisibility(View.GONE);
        }


        //将之前自定义好的信息显示出来
        String replyPageFrameImageType=(String)list.get(position).get("replyPageFrameImageType");
        if(replyPageFrameImageType.equals("bitmap"))
            holder.replayPageFrameImage.setImageBitmap((Bitmap) list.get(position).get("replyPageFrameImage"));
        else if(replyPageFrameImageType.equals("drawable"))
            holder.replayPageFrameImage.setBackgroundResource((Integer)list.get(position).get("replyPageFrameImage"));
        holder.replayPageFrameName.setText((String) list.get(position).get("replyPageFrameName"));
        holder.replayPageFrameHint.setText((String) list.get(position).get("replyPageFrameHint"));
        holder.addReplyPageChooseFlag.setBackgroundResource((Integer) list.get(position).get("addReplyPageChooseFlag"));
        list.get(position).put("addReplyPageChooseFlagView", holder.addReplyPageChooseFlag);//便于之后设置可见性
        return convertView;

    }

}

