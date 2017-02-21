package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xiangjun on 15/11/24.
 */
public class EditDeviceAdapter extends BaseAdapter {

    ArrayList<HashMap<String, Object>> ls;
    EditDeviceFrame editDeviceFrame;
    LayoutInflater inflater;
    TextView tex;
    final int VIEW_TYPE = 4;
    final int TYPE_1 = 0;//设备备注名
    final int TYPE_2 = 1;//Major和Minor
    final int TYPE_3 = 2;//带有图标的style
    final int TYPE_4 = 3;//绑定在该设备上的页面列表

    ViewHolder1 holder1 = null;
    ViewHolder2 holder2 = null;
    ViewHolder3 holder3 = null;
    ViewHolder4 holder4 = null;

    private List<Map<String, Object>> editDeviceFrameList;
    private EditDeviceFrameListAdapter editDeviceFrameListAdapter;

    private String token;
    private String[] relationalPagesId;
    private Handler deleteFrameHandler;


    public EditDeviceAdapter(EditDeviceFrame editDeviceFrame,
                             ArrayList<HashMap<String, Object>> list,String token,String[] relationalPagesId,Handler deleteFrameHandler) {
        this.ls = list;
        this.editDeviceFrame = editDeviceFrame;
        this.token=token;
        this.relationalPagesId=relationalPagesId;
        this.deleteFrameHandler=deleteFrameHandler;

    }

    public ListView getFrameOnThisDeviceList(){
        return holder4.frameOnThisDeviceList;
    }

    public EditDeviceFrameListAdapter getEditDeviceFrameListAdapter() {
        return editDeviceFrameListAdapter;
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    public void setItem(int index,String key,String value){
        HashMap<String, Object> map=ls.get(index);
        map.remove(key);
        map.put(key, value);
    }

    public Object getItemAtMap(int index,String key){
        HashMap<String, Object> map=ls.get(index);
        return map.get(key);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)//设备备注名
            return TYPE_1;
        else if (p == 1 || p == 2)//Major和Minor
            return TYPE_2;
        else if (p == 3 || p == 4)//带有图标的style
            return TYPE_3;
        else if (p == 5)//绑定在该设备上的页面列表
            return TYPE_4;
        else
            return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        if (convertView == null) {
            inflater = LayoutInflater.from(editDeviceFrame);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.edit_device_item_style1,
                            parent, false);
                    holder1 = new ViewHolder1();
                    holder1.deviceRemarksTitle = (TextView) convertView
                            .findViewById(R.id.deviceRemarksTitleStyle);
                    holder1.deviceRemarksName = (TextView) convertView
                            .findViewById(R.id.deviceRemarksNameStyle);
                    holder1.deviceRemarksArrow = (ImageView) convertView
                            .findViewById(R.id.deviceRemarksArrow);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.edit_device_item_style2,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.MajorMinorTitle = (TextView) convertView
                            .findViewById(R.id.MajorMinorTitleStyle);
                    holder2.MajorMinorNumber = (TextView) convertView
                            .findViewById(R.id.MajorMinorNumberStyle);
                    convertView.setTag(holder2);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.edit_device_item_style3,
                            parent, false);
                    holder3 = new ViewHolder3();
                    holder3.WithIconTitle = (TextView) convertView
                            .findViewById(R.id.WithIconTitleStyle);
                    holder3.Icon = (ImageView) convertView
                            .findViewById(R.id.IconStyle);
                    convertView.setTag(holder3);
                    break;
                case TYPE_4:
                    convertView = inflater.inflate(R.layout.edit_device_item_style4,
                            parent, false);
                    holder4 = new ViewHolder4();
                    holder4.frameOnThisDeviceList = (ListView) convertView
                            .findViewById(R.id.frameOnThisDeviceListStyle);
                    convertView.setTag(holder4);
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
            }
        }
        // 设置资源
        switch (type) {
            case TYPE_1://设备备注名
                holder1.deviceRemarksTitle.setText((String) ls.get(position).get("deviceRemarksTitle"));
                holder1.deviceRemarksName.setText((String) ls.get(position).get("deviceRemarksName"));
                holder1.deviceRemarksArrow.setBackgroundResource((Integer) ls.get(position).get("deviceRemarksArrow"));
                break;
            case TYPE_2://Major和Minor
                holder2.MajorMinorTitle.setText((String) ls.get(position).get("MajorMinorTitle"));
                holder2.MajorMinorNumber.setText((String) ls.get(position).get("MajorMinorNumber"));
                break;
            case TYPE_3://带有图标的style
                holder3.WithIconTitle.setText((String) ls.get(position).get("WithIconTitle"));
                holder3.Icon.setBackgroundResource((Integer) ls.get(position).get("Icon"));
                break;
            case TYPE_4://这个设备上的页面列表
                editDeviceFrameList = new ArrayList<Map<String, Object>>();

                editDeviceFrameListAdapter = new EditDeviceFrameListAdapter(editDeviceFrameList, editDeviceFrame,holder4.frameOnThisDeviceList,deleteFrameHandler);

                RequestData myDeviceRequestData=new RequestData(null,null,null,null,relationalPagesId,"1",null,null,null,null,null,null,null);
                ConnectRequest myDeviceConnectrequest = new ConnectRequest(token,editDeviceFrameListAdapter,holder4.frameOnThisDeviceList,editDeviceFrame);
                myDeviceConnectrequest.RequestAPI(RequestKind.SearchPageList, myDeviceRequestData);

                break;

        }

        return convertView;
    }

    public class ViewHolder1 {
        TextView deviceRemarksTitle;
        TextView deviceRemarksName;
        ImageView deviceRemarksArrow;
    }

    public class ViewHolder2 {
        TextView MajorMinorTitle;
        TextView MajorMinorNumber;
    }

    public class ViewHolder3 {
        TextView WithIconTitle;
        ImageView Icon;
    }

    public class ViewHolder4 {
        ListView frameOnThisDeviceList;
    }


}
