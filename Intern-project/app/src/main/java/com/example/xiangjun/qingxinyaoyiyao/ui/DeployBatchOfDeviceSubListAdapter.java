package com.example.xiangjun.qingxinyaoyiyao.ui;

/**
 * Created by xiangjun on 15/11/24.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;

public class DeployBatchOfDeviceSubListAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;
    private int maxIndex = 1;
    private ArrayList<Integer> selectedIndexList = new ArrayList<Integer>();
    private ArrayList<Integer> hasDeployedSuccefullyIndex = new ArrayList<Integer>();
    private ArrayList<Integer> existedIndexList = new ArrayList<Integer>();
    private ArrayList<Integer> deploySuccessfullyIndexPageNumber = new ArrayList<Integer>();

    private boolean chooseAllBtnHasBeenClicked = false;

    private int lastLoadingMaxIndex=0;
    private boolean hasFinishedLoading=false;
    private boolean hasDeployed=false;

    public DeployBatchOfDeviceSubListAdapter(List<Map<String, Object>> list, Context context) {
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    //自定义holder来装其他组件
    public final class ViewHolder {
        public ImageView chooseFlag;
        public TextView deployBatchOfDeviceSubListItemName;
        public TextView deployBatchOfDeviceSubListItemNumber;
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

    public void addInSuccessList(int selectedIndex) {
        hasDeployedSuccefullyIndex.add(selectedIndex);
    }

    public void addInExistedList(int selectedIndex) {
        existedIndexList.add(selectedIndex);
    }

    public void addIndeploySuccessfullyIndexPageNumberList(int selectedIndex) {
        deploySuccessfullyIndexPageNumber.add(selectedIndex);
    }

    public void deleteSelectedIndex(int toDeleteIndex) {
        for (int i = 0; i < selectedIndexList.size(); i++) {
            if (selectedIndexList.get(i) == toDeleteIndex) {
                selectedIndexList.remove(i);
                break;
            }
        }
    }
    public void deleteSuccessIndex(int toDeleteIndex) {
        for (int i = 0; i < hasDeployedSuccefullyIndex.size(); i++) {
            if (hasDeployedSuccefullyIndex.get(i) == toDeleteIndex) {
                hasDeployedSuccefullyIndex.remove(i);
                break;
            }
        }
    }

    public void deleteExistedIndex(int toDeleteIndex) {
        for (int i = 0; i < existedIndexList.size(); i++) {
            if (existedIndexList.get(i) == toDeleteIndex) {
                existedIndexList.remove(i);
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

    public boolean isInSuccessList(int index) {
        for (int i = 0; i < hasDeployedSuccefullyIndex.size(); i++) {
            if (hasDeployedSuccefullyIndex.get(i) == index)
                return true;
        }
        return false;
    }

    public boolean isInExistedList(int index) {
        for (int i = 0; i < existedIndexList.size(); i++) {
            if (existedIndexList.get(i) == index)
                return true;
        }
        return false;
    }

    public void clearSuccessList() {
        hasDeployedSuccefullyIndex.clear();
    }

    public void clearExistedList() {
        existedIndexList.clear();
    }

    public void clearDeploySuccessfullyIndexPageNumberList() {
        deploySuccessfullyIndexPageNumber.clear();
    }

    public void clearSelectedList() {
        selectedIndexList.clear();
    }

    public void setChooseAllBtnHasBeenClicked(boolean chooseAllBtnHasBeenClicked) {
        this.chooseAllBtnHasBeenClicked = chooseAllBtnHasBeenClicked;
    }

    public ArrayList<Integer> getSelectedIndexList() {
        return selectedIndexList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public ArrayList<Integer> getHasDeployedSuccefullyIndex() {
        return hasDeployedSuccefullyIndex;
    }

    public ArrayList<Integer> getExistedIndexList() {
        return existedIndexList;
    }

    public void addItem(Map<String, Object> map) {
        list.add(map);
    }

    public void addItemAtMap(int index, String key, String value) {
        Map<String, Object> map = list.get(index);
        map.put(key, value);
    }

    public void setItem(int index, String key, Object value) {
        Map<String, Object> map = list.get(index);
        map.remove(key);
        map.put(key, value);
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

            convertView = mInflater.inflate(R.layout.deploy_batch_of_device_style5_item, null);
            holder.chooseFlag = (ImageView) convertView.findViewById(R.id.chooseFlag);
            holder.deployBatchOfDeviceSubListItemName = (TextView) convertView.findViewById(R.id.deployBatchOfDeviceSubListItemName);
            holder.deployBatchOfDeviceSubListItemNumber = (TextView) convertView.findViewById(R.id.deployBatchOfDeviceSubListItemNumber);
            if (position > maxIndex)
                maxIndex = position;

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        if (chooseAllBtnHasBeenClicked) {
            holder.chooseFlag.setVisibility(View.VISIBLE);
        } else {
            if (isInSelectedList(position)) {
                if (isInSuccessList(position)) {//部署成功
                    holder.chooseFlag.setVisibility(View.GONE);
                    holder.deployBatchOfDeviceSubListItemNumber.setText(deploySuccessfullyIndexPageNumber.get(position)+"个页面");
                    deleteSelectedIndex(position);
                    hasDeployed=true;
                } else if (isInExistedList(position)) {//已存在（忽略部署）
                    holder.chooseFlag.setVisibility(View.GONE);
                    deleteSelectedIndex(position);
                } else//部署不成功
                    holder.chooseFlag.setVisibility(View.VISIBLE);
            } else//以防不测
                holder.chooseFlag.setVisibility(View.GONE);
        }

        //将之前自定义好的信息显示出来
        holder.chooseFlag.setBackgroundResource((Integer) list.get(position).get("chooseFlagResource"));
        holder.deployBatchOfDeviceSubListItemName.setText((String) list.get(position).get("deployBatchOfDeviceSubListItemName"));
        if(!hasDeployed)
            holder.deployBatchOfDeviceSubListItemNumber.setText((String) list.get(position).get("deployBatchOfDeviceSubListItemNumber"));
        list.get(position).put("chooseFlagView", holder.chooseFlag);//便于之后设置可见性

        return convertView;

    }

}

