package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.ConnectRequest;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestData;
import com.example.xiangjun.qingxinyaoyiyao.function.RequestKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by xiangjun on 15/11/24.
 */
public class DeployBatchOfDeviceAdapter extends BaseAdapter {

    ArrayList<HashMap<String, Object>> ls;
    DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame;
    LayoutInflater inflater;
    TextView tex;
    final int VIEW_TYPE = 5;
    final int TYPE_1 = 0;//页面信息
    final int TYPE_2 = 1;//“选择部署方式”静态信息
    final int TYPE_3 = 2;//segment
    final int TYPE_4 = 3;//两个按钮
    final int TYPE_5 = 4;//可选的设备列表

    ViewHolder1 holder1 = null;
    ViewHolder2 holder2 = null;
    ViewHolder3 holder3 = null;
    ViewHolder4 holder4 = null;
    ViewHolder5 holder5 = null;

    ArrayList<Map<String, Object>> deployBatchOfDeviceTextList;
    DeployBatchOfDeviceSubListAdapter deployBatchOfDeviceSubListAdapter;
    String token;
    String frameId;

    boolean isFirstTimeLoad=true;

    public DeployBatchOfDeviceAdapter(DeployBatchOfDeviceFrame thisDeployBatchOfDeviceFrame,
                                      ArrayList<HashMap<String, Object>> list,String token,String frameId) {
        ls = list;
        this.thisDeployBatchOfDeviceFrame = thisDeployBatchOfDeviceFrame;
        this.token=token;
        this.frameId=frameId;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        int p = position;
        if (p == 0)//页面信息
            return TYPE_1;
        else if (p == 1)//"选择部署方式"静态信息
            return TYPE_2;
        else if (p == 2)//segment
            return TYPE_3;
        else if (p == 3)//两个按钮
            return TYPE_4;
        else if (p == 4)//可绑定的设备列表
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
            inflater = LayoutInflater.from(this.thisDeployBatchOfDeviceFrame);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.deploy_batch_of_device_style1,
                            parent, false);
                    holder1 = new ViewHolder1();
                    holder1.deployBatchOfDeviceFrameName = (TextView) convertView
                            .findViewById(R.id.deployBatchOfDeviceFrameName);
                    holder1.deployBatchOfDeviceFrameHint = (TextView) convertView
                            .findViewById(R.id.deployBatchOfDeviceFrameHint);
                    holder1.deployBatchOfDeviceFrameImage = (ImageView) convertView
                            .findViewById(R.id.deployBatchOfDeviceFrameImage);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.deploy_batch_of_device_style2,
                            parent, false);
                    holder2 = new ViewHolder2();
                    holder2.deployBatchOfDeviceStaticInfo = (TextView) convertView
                            .findViewById(R.id.deployBatchOfDeviceStaticInfoStyle);
                    convertView.setTag(holder2);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.deploy_batch_of_device_style3,
                            parent, false);
                    holder3 = new ViewHolder3();
                    holder3.deployBatchOfDeviceSegment = (DeployBatchOfDeviceSegment) convertView
                            .findViewById(R.id.deployBatchOfDeviceSegment);
                    convertView.setTag(holder3);
                    break;
                case TYPE_4:
                    convertView = inflater.inflate(R.layout.deploy_batch_of_device_style4,
                            parent, false);
                    holder4 = new ViewHolder4();
                    holder4.chooseAllBtn = (Button) convertView
                            .findViewById(R.id.chooseAllBtn);
                    holder4.startDeployBtn = (Button) convertView
                            .findViewById(R.id.startDeployBtn);
                    convertView.setTag(holder4);
                    break;
                case TYPE_5:
                    convertView = inflater.inflate(R.layout.deploy_batch_of_device_style5,
                            parent, false);
                    holder5 = new ViewHolder5();
                    holder5.deployBatchOfDeviceSubList = (RefreshableListView)convertView
                            .findViewById(R.id.deployBatchOfDeviceSubListStyle);
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
            case TYPE_1://页面信息
                holder1.deployBatchOfDeviceFrameName.setText((String) ls.get(position).get("deployBatchOfDeviceFrameName"));
                holder1.deployBatchOfDeviceFrameHint.setText((String) ls.get(position).get("deployBatchOfDeviceFrameHint"));
                holder1.deployBatchOfDeviceFrameImage.setImageBitmap((Bitmap) ls.get(position).get("deployBatchOfDeviceFrameImage"));
                break;
            case TYPE_2://"选择部署方式"静态信息
                holder2.deployBatchOfDeviceStaticInfo.setText((String) ls.get(position).get("deployBatchOfDeviceStaticInfo"));
                break;
            case TYPE_3://带有图标的style
                Context context=(Context)ls.get(position).get("deployBatchOfDeviceSegmentContext");

                holder3.deployBatchOfDeviceSegment=new DeployBatchOfDeviceSegment(context);
                break;
            case TYPE_4:
                holder4.chooseAllBtn.setText((String) ls.get(position).get("chooseAllBtn"));
                holder4.startDeployBtn.setText((String) ls.get(position).get("startDeployBtn"));
                break;
            case TYPE_5://这个设备上的页面列表
                if(isFirstTimeLoad) {
                    RequestData deployOnDeviceRequestData = new RequestData("0", "20", null, null, null, "2", null, null, null, null, null, null, null);
                    ConnectRequest deployOnDeviceConnectrequest = new ConnectRequest(getToken(), holder5.deployBatchOfDeviceSubList, null,
                            thisDeployBatchOfDeviceFrame,holder3.deployBatchOfDeviceSegment,holder4.chooseAllBtn,holder4.startDeployBtn,frameId);
                    deployOnDeviceConnectrequest.RequestAPI(RequestKind.SearchDevice, deployOnDeviceRequestData);
                    isFirstTimeLoad=false;
                }

                break;

        }

        return convertView;
    }

    public class ViewHolder1 {
        ImageView deployBatchOfDeviceFrameImage;
        TextView deployBatchOfDeviceFrameName;
        TextView deployBatchOfDeviceFrameHint;

    }

    public class ViewHolder2 {
        TextView deployBatchOfDeviceStaticInfo;
    }

    public class ViewHolder3 {
        DeployBatchOfDeviceSegment deployBatchOfDeviceSegment;
    }

    public class ViewHolder4 {
        Button chooseAllBtn;
        Button startDeployBtn;
    }

    public class ViewHolder5 {
        RefreshableListView deployBatchOfDeviceSubList;
    }


}
