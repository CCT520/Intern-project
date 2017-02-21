package com.example.xiangjun.qingxinyaoyiyao.ui;

/**
 * Created by xiangjun on 15/11/24.
 */

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

public class QuicklyGetPageLinkGridViewAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;
    private int selectedIndex = -1;
    private int maxIndex=0;

    private boolean hasFinishedLoading=false;

    public QuicklyGetPageLinkGridViewAdapter(List<Map<String, Object>> list, Context context) {
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

    }

    public boolean isHasFinishedLoading() {
        return hasFinishedLoading;
    }

    public void setHasFinishedLoading(boolean hasFinishedLoading) {
        this.hasFinishedLoading = hasFinishedLoading;
    }

    public List<Map<String, Object>> getList() {
        return this.list;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    //自定义holder来装按钮与其他组件
    public final class ViewHolder {
        public LinearLayout linkItemLinearLayout;
        public TextView linkItemTitle;
        public TextView linkItemUpdateTime;
        public ImageView linkItemPicture;
        public TextView linkItemDigest;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public void addItem(Map<String, Object> map) {
        list.add(0, map);
    }

    public void addItemAtMap(int index, String key, String value) {
        Map<String, Object> map = list.get(index);
        map.put(key, value);
    }

    public void addItemsAtMap(int index, String key, String[] value) {
        Map<String, Object> map = list.get(index);
        map.put(key, value);
    }

    public void setItem(int index, String key, Object value) {
        Map<String, Object> map = list.get(index);
        map.remove(key);
        map.put(key, value);
    }

    public void setImage(int index, Bitmap bitmap) {
        Map<String, Object> map = list.get(index);
        map.remove("linkItemPicture");
        map.put("linkItemPicture", bitmap);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            //初始化holder的各个变量
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.quickly_get_page_link_gridview_item, null);
            holder.linkItemLinearLayout = (LinearLayout) convertView.findViewById(R.id.linkItemLinearLayout);
            holder.linkItemTitle = (TextView) convertView.findViewById(R.id.linkItemTitle);
            holder.linkItemUpdateTime = (TextView) convertView.findViewById(R.id.linkItemUpdateTime);
            holder.linkItemPicture = (ImageView) convertView.findViewById(R.id.linkItemPicture);
            holder.linkItemDigest = (TextView) convertView.findViewById(R.id.linkItemDigest);

            convertView.setTag(holder);


        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if (position == selectedIndex) {
            holder.linkItemLinearLayout.setBackgroundResource(R.drawable.quickly_get_page_link_selected_item_shape);
            holder.linkItemTitle.setTextColor(Color.WHITE);
            holder.linkItemUpdateTime.setTextColor(Color.WHITE);
//            holder.linkItemPicture.setBackgroundColor(Color.WHITE);
            holder.linkItemDigest.setTextColor(Color.WHITE);
        } else {
            holder.linkItemLinearLayout.setBackgroundResource(R.drawable.quickly_get_page_link_not_selected_item_shape);
            holder.linkItemTitle.setTextColor(Color.BLACK);
            holder.linkItemUpdateTime.setTextColor(Color.BLACK);
            holder.linkItemDigest.setTextColor(Color.BLACK);
        }


        holder.linkItemTitle.setText((String) list.get(position).get("linkItemTitle"));
        holder.linkItemUpdateTime.setText((String) list.get(position).get("linkItemUpdateTime"));
        holder.linkItemPicture.setImageBitmap((Bitmap) list.get(position).get("linkItemPicture"));
        holder.linkItemDigest.setText((String) list.get(position).get("linkItemDigest"));


        list.get(position).put("linkItemLinearLayout", holder.linkItemLinearLayout);
        list.get(position).put("linkItemTitleTv", holder.linkItemTitle);
        list.get(position).put("linkItemUpdateTimeTv", holder.linkItemUpdateTime);
//        list.get(position).put("linkItemPicture", holder.linkItemPicture);
        list.get(position).put("linkItemDigestTv", holder.linkItemDigest);

        if(position>maxIndex)
            maxIndex=position;


        return convertView;

    }

}

