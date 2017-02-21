package com.example.xiangjun.qingxinyaoyiyao.ui;

/**
 * Created by xiangjun on 15/11/24.
 */

import java.io.ByteArrayOutputStream;
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

public class FrameLibAdapter extends BaseAdapter {

    private final static int EDITPAGE_REQUEST_CODE = 10;
    private final static int EDITPAGE_RESULT_CODE = 10;

    private List<Map<String, Object>> list;
    private MainFrame thisMainFrame;
    private FrameLibAdapter thisFrameLibAdapter;
    private RefreshableListView thisFrameLiblv;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;
    private float origin_x, later_x;
    private String token;

    private int lastLoadingMaxIndex = 0;
    private int firstVisibleItem=0;
    private boolean hasFinishedLoading = false;
    private int selectedIndex = -1;

    public FrameLibAdapter(List<Map<String, Object>> list, RefreshableListView thisFrameLiblv, MainFrame thisMainFrame, String token) {
        this.list = list;
        this.thisMainFrame = thisMainFrame;
        this.mInflater = LayoutInflater.from(this.thisMainFrame);
        this.token = token;
        this.thisFrameLibAdapter = this;
        this.thisFrameLiblv = thisFrameLiblv;
    }

    public int getFirstVisibleItem() {
        return firstVisibleItem;
    }

    public void setFirstVisibleItem(int firstVisibleItem) {
        this.firstVisibleItem = firstVisibleItem;
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

    public FrameLibAdapter getThisFrameLibAdapter() {
        return thisFrameLibAdapter;
    }

    public RefreshableListView getThisFrameLiblv() {
        return thisFrameLiblv;
    }

    //自定义holder来装其他组件
    public final class ViewHolder {
        public ImageView frameImage;
        public TextView frameName;
        public TextView frameHint;
        public Button editBtn;
        public Button deleteBtn;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void addItem(Map<String, Object> map) {
        list.add(map);
    }


    public void setItemAtMap(int position, String key, Object value) {
        Map<String, Object> map = list.get(position);
        map.remove(key);
        map.put(key, value);
    }

    public void addItemAtMap(int position, String key, Object value) {
        Map<String, Object> map = list.get(position);
        map.put(key, value);
    }

    public void deleteItem(int position) {
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
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.framelib_list_item, null);
            holder.frameImage = (ImageView) convertView.findViewById(R.id.frameImage);
            holder.frameName = (TextView) convertView.findViewById(R.id.frameName);
            holder.frameHint = (TextView) convertView.findViewById(R.id.frameHint);
            holder.editBtn = (Button) convertView.findViewById(R.id.editBtn);
            holder.deleteBtn = (Button) convertView.findViewById(R.id.deleteBtn);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

//        if (position != selectedIndex) {
//            holder.deleteBtn.setVisibility(View.GONE);
//            holder.editBtn.setVisibility(View.GONE);
//        } else {
//            holder.deleteBtn.setVisibility(View.VISIBLE);
//            holder.editBtn.setVisibility(View.VISIBLE);
//        }


        //将之前自定义好的信息显示出来
        String frameImageType = (String) (String) list.get(position).get("frameImageType");
        if (frameImageType.equals("bitmap"))
            holder.frameImage.setImageBitmap((Bitmap) list.get(position).get("frameImage"));
        else if (frameImageType.equals("drawable"))
            holder.frameImage.setBackgroundResource((Integer) list.get(position).get("frameImage"));
        holder.frameName.setText((String) list.get(position).get("frameName"));
        holder.frameHint.setText((String) list.get(position).get("frameHint"));
        list.get(position).put("deleteBtn", holder.deleteBtn);
        list.get(position).put("editBtn", holder.editBtn);


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(thisMainFrame);  //先得到构造器
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

                        for (int i = 0; i < getCount(); i++) {
                            if (i == selectedIndex) {
                                Button deleteBtn = (Button) list.get(i).get("deleteBtn");
                                deleteBtn.setVisibility(View.GONE);
                                Button editBtn = (Button) list.get(i).get("editBtn");
                                editBtn.setVisibility(View.GONE);
                                selectedIndex=-1;
                                break;
                            }
                        }

                        Map<String, Object> map = (Map<String, Object>) getItem(position);

                        String[] page_ids = {(String) map.get("frameId")};

                        RequestData deletePageRequestData = new RequestData(null, null, null, null, page_ids, null, null, null, null, null, null, null, null);
                        ConnectRequest deleteConnectRequest = new ConnectRequest(token, thisMainFrame, getThisFrameLibAdapter(), getThisFrameLiblv(), position);

                        deleteConnectRequest.RequestAPI(RequestKind.PageDelete, deletePageRequestData);

                    }
                });
                //参数都设置完成了，创建并显示出来
                builder.create().show();
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < getCount(); i++) {
                    if (i == selectedIndex) {
                        Button deleteBtn = (Button) list.get(i).get("deleteBtn");
                        deleteBtn.setVisibility(View.GONE);
                        Button editBtn = (Button) list.get(i).get("editBtn");
                        editBtn.setVisibility(View.GONE);
                        break;
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("token", token);
                intent.putExtra("title", "编辑页面");
                intent.putExtra("operationType", "editPage");

                Map<String, Object> map = (Map<String, Object>) getItem(position);

                Bitmap originalThumbnailBitmap = (Bitmap) map.get("frameImage");
                //把获得的bitmap对象转换成byte数组以便传递
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                originalThumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] originalThumbnailBytes = baos.toByteArray();

                String originalPageName = (String) map.get("pageName");
                String originalMainTitle = (String) map.get("frameName");
                String originalSubTitle = (String) map.get("frameHint");
                String originalPageLink = (String) map.get("pageLink");
                String originalIconUrl = (String) map.get("iconUrl");
                String pageId = (String) map.get("frameId");

                intent.putExtra("originalThumbnailBytes", originalThumbnailBytes);
                intent.putExtra("originalPageName", originalPageName);
                intent.putExtra("originalMainTitle", originalMainTitle);
                intent.putExtra("originalSubTitle", originalSubTitle);
                intent.putExtra("originalPageLink", originalPageLink);
                intent.putExtra("originalIconUrl", originalIconUrl);
                intent.putExtra("pageId", pageId);
                intent.putExtra("token", token);
                intent.putExtra("editIndex", position);

                intent.setClass(thisMainFrame, AddOrEditPageFrame.class);
                thisMainFrame.startActivityForResult(intent, EDITPAGE_REQUEST_CODE);
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
                                Button deleteBtn = (Button) list.get(i).get("deleteBtn");
                                deleteBtn.setVisibility(View.GONE);
                                Button editBtn = (Button) list.get(i).get("editBtn");
                                editBtn.setVisibility(View.GONE);
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP://松开
                        later_x = event.getX();
                        if (later_x - origin_x < -2) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            holder.editBtn.setVisibility(View.VISIBLE);
                            holder.deleteBtn.setVisibility(View.VISIBLE);
                            selectedIndex = position;
                        } else if (later_x - origin_x > 2) {
                            ViewHolder holder = (ViewHolder) v.getTag();
                            holder.editBtn.setVisibility(View.GONE);
                            holder.deleteBtn.setVisibility(View.GONE);
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

        String frameName = (String) selectedItemMap.get("frameName");
        String frameHint = (String) selectedItemMap.get("frameHint");
        String frameId = (String) selectedItemMap.get("frameId");
        Bitmap pageImage = (Bitmap) selectedItemMap.get("frameImage");


        //把获得的bitmap对象转换成byte数组以便传递
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pageImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] pageImageBytes = baos.toByteArray();


        Intent intent = new Intent();
        intent.putExtra("frameName", frameName);
        intent.putExtra("frameHint", frameHint);
        intent.putExtra("frameId", frameId);
        intent.putExtra("frameImage", pageImageBytes);
        intent.putExtra("token", token);
        intent.setClass(thisMainFrame, DeployBatchOfDeviceFrame.class);
        thisMainFrame.startActivity(intent);
    }

}

