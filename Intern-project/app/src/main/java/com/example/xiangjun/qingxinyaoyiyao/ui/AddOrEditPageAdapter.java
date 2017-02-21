package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangjun on 15/12/22.
 */


public class AddOrEditPageAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<Map<String, Object>> mData;

    final int TYPE_1 = 0;//缩略图的style
    final int TYPE_2 = 1;//相关账号信息文本
    private ViewHolder1 holder1 = null;
    private ViewHolder2 holder2 = null;

    public AddOrEditPageAdapter(Context context, List<Map<String, Object>> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setImage(Bitmap value,String imgPath){
        Map<String, Object> map=mData.get(0);
        map.remove("thumbnailType");
        map.put("thumbnailType", "bitmap");
        map.remove("thumbnail");
        map.put("thumbnail", value);
        map.put("thumbnailPath",imgPath);
    }

    public void setItem(int index, String key, String value) {
        Map<String, Object> map = mData.get(index);
        map.remove(key);
        map.put(key, value);
    }

    public Object getNamedItem(int index,String key){
        Map<String, Object> map = mData.get(index);
        return map.get(key);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)//缩略图的style
            return TYPE_1;
        else if (p == 1 || p == 2 || p == 3 || p == 4)//页面相关信息
            return TYPE_2;
        else
            return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        int type = getItemViewType(position);
        if (convertView == null) {
            mInflater = LayoutInflater.from(context);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = mInflater.inflate(R.layout.add_or_edit_page_item1,
                            parent, false);
                    holder1 = new ViewHolder1();
                    holder1.thumbnailTitle = (TextView) convertView
                            .findViewById(R.id.thumbnailTitle);
                    holder1.thumbnail = (ImageView) convertView
                            .findViewById(R.id.thumbnail);
                    holder1.thumbnailArrow = (ImageView) convertView
                            .findViewById(R.id.thumbnailArrow);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = mInflater.inflate(R.layout.add_or_edit_page_item2,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.pageInfoTitle = (TextView) convertView
                            .findViewById(R.id.pageInfoTitle);
                    holder2.pageInfo = (TextView) convertView
                            .findViewById(R.id.pageInfo);
                    holder2.pageInfoArrow = (ImageView) convertView
                            .findViewById(R.id.pageInfoArrow);
                    convertView.setTag(holder2);
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
            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1://缩略图的style
                holder1.thumbnailTitle.setText((String) mData.get(position).get("thumbnailTitle"));
                String thumbnailType=((String) mData.get(position).get("thumbnailType"));
                if(thumbnailType.equals("drawable"))
                    holder1.thumbnail.setBackgroundResource((Integer) mData.get(position).get("thumbnail"));
                else if(thumbnailType.equals("bitmap"))
                    holder1.thumbnail.setImageBitmap((Bitmap) mData.get(position).get("thumbnail"));
                holder1.thumbnailArrow.setBackgroundResource((Integer) mData.get(position).get("thumbnailArrow"));
                break;
            case TYPE_2://页面信息
                switch (position) {
                    case 1://页面名称
                        holder2.pageInfoTitle.setText((String) mData.get(position).get("pageNameTitle"));
                        holder2.pageInfo.setText((String) mData.get(position).get("pageName"));
                        holder2.pageInfoArrow.setBackgroundResource((Integer) mData.get(position).get("pageNameArrow"));
                        break;
                    case 2://主标题
                        holder2.pageInfoTitle.setText((String) mData.get(position).get("pageMainTitle"));
                        holder2.pageInfo.setText((String) mData.get(position).get("pageMainTitleInfo"));
                        holder2.pageInfoArrow.setBackgroundResource((Integer) mData.get(position).get("pageMainTitleArrow"));
                        break;
                    case 3://副标题
                        holder2.pageInfoTitle.setText((String) mData.get(position).get("pageSubTitle"));
                        holder2.pageInfo.setText((String) mData.get(position).get("pageSubTitleInfo"));
                        holder2.pageInfoArrow.setBackgroundResource((Integer) mData.get(position).get("pageSubTitleArrow"));
                        break;
                    case 4://页面链接
                        holder2.pageInfoTitle.setText((String) mData.get(position).get("pageLinkTitle"));
                        holder2.pageInfo.setText((String) mData.get(position).get("pageLink"));
                        holder2.pageInfoArrow.setBackgroundResource((Integer) mData.get(position).get("pageLinkArrow"));
                        break;

                }


                break;
        }

        return convertView;
    }

    //自定义holder来装按钮与其他组件
    public final class ViewHolder1 {
        public TextView thumbnailTitle;
        public ImageView thumbnail;//缩略图
        public ImageView thumbnailArrow;//箭头
    }

    public final class ViewHolder2 {
        public TextView pageInfoTitle;
        public TextView pageInfo;//相关页面信息
        public ImageView pageInfoArrow;//箭头
    }


}



