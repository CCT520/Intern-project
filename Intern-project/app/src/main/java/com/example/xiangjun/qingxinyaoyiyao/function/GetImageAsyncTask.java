package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.ui.AddReplyPageFrameAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrameListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.FrameLibAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.MyInfoAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.QuicklyGetPageLinkGridViewAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.QuicklyGetPageLinkFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by xiangjun on 15/12/9.
 */
public class GetImageAsyncTask extends AsyncTask<Void, String, Void> {

    private final static int QUICKLY_SET_PAGELINK_RESULT_CODE = 10;

    private String URL;
    private Bitmap pageImage = null;
    private EditDeviceFrameListAdapter editDeviceFrameListAdapter;
    private ListView frameOnThisDeviceList;

    private FrameLibAdapter frameLibAdapter;
    private RefreshableListView frameLibLv;

    private MyInfoAdapter myInfoAdapter;
    private ListView myInfoLv;

    private QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter;
    private GridView imageTextGridView;
    private int index;
    private Button finishPageLinkBtn;
    private QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame;
    private int selectedItemIndex;

    private AddReplyPageFrameAdapter addReplyPageFrameAdapter;
    private RefreshableListView addReplyPagelv;
    private AlertDialog isLoadingDialog;
    private RefreshableListView quicklyGetPageLinkList;

    private String type = "";


    //这是由id获取页面的构造函数
    public GetImageAsyncTask(String URL, int index,
                             EditDeviceFrameListAdapter editDeviceFrameListAdapter, ListView frameOnThisDeviceList) {
        this.URL = URL;
        this.index = index;
        this.editDeviceFrameListAdapter = editDeviceFrameListAdapter;
        this.frameOnThisDeviceList = frameOnThisDeviceList;
        type = "fromId";
    }

    //这是从头获取页面的构造函数(主页面的页面库里的)
    public GetImageAsyncTask(String URL, int index,
                             FrameLibAdapter frameLibAdapter, RefreshableListView frameLibLv
                             ) {
        this.URL = URL;
        this.index = index;
        this.frameLibAdapter = frameLibAdapter;
        this.frameLibLv = frameLibLv;
        type = "fromBegin";
    }

    //这是从头获取页面的构造函数(添加回复页面上的)
    public GetImageAsyncTask(String URL, int index,
                             AddReplyPageFrameAdapter addReplyPageFrameAdapter, RefreshableListView addReplyPagelv) {
        this.URL = URL;
        this.index = index;
        this.addReplyPageFrameAdapter = addReplyPageFrameAdapter;
        this.addReplyPagelv = addReplyPagelv;
        type = "fromBeginOnAddReplyPage";
    }

    public GetImageAsyncTask(String URL, MyInfoAdapter myInfoAdapter, ListView myInfoLv) {
        this.URL = URL;
        this.myInfoAdapter = myInfoAdapter;
        this.myInfoLv = myInfoLv;

        type = "myInfo";
    }

    //快速获取页面链接的构造函数
    public GetImageAsyncTask(String URL, int index, RefreshableListView quicklyGetPageLinkList,GridView imageTextGridView,
                             QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter, Button finishPageLinkBtn,
                             QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame,AlertDialog isLoadingDialog) {
        this.URL = URL;
        this.index = index;
        this.quicklyGetPageLinkList=quicklyGetPageLinkList;
        this.imageTextGridView = imageTextGridView;
        this.quicklyGetPageLinkGridViewAdapter = quicklyGetPageLinkGridViewAdapter;
        this.finishPageLinkBtn = finishPageLinkBtn;
        this.thisQuicklyGetPageLinkFrame = thisQuicklyGetPageLinkFrame;
        this.isLoadingDialog=isLoadingDialog;
        type = "linkItemPicture";
    }

    /**
     * 从指定URL获取图片
     *
     * @param url
     * @return
     */
    public Bitmap getImageBitmap(String url) throws IOException {
        java.net.URL imgUrl = null;
        Bitmap bitmap = null;

        imgUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
        conn.setDoInput(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        bitmap = BitmapFactory.decodeStream(is);
        is.close();

        return bitmap;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            pageImage = getImageBitmap(URL);

            switch (type) {
                case "fromId":
                    publishProgress("setPageImageFromId");
                    break;
                case "fromBegin":
                    publishProgress("setPageImageFromBegin");
                    break;
                case "myInfo":
                    publishProgress("setPageImageForMyInfo");
                    break;
                case "linkItemPicture":
                    publishProgress("setPageImageLinkItem");
                    break;
                case "fromBeginOnAddReplyPage":
                    publishProgress("setPageImageFromBeginForAddReplyPage");
                    break;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {

        switch (progress[0]) {
            case "setPageImageFromId":
                editDeviceFrameListAdapter.setItemAtMap(index, "editDeviceFrameImage", pageImage);
                editDeviceFrameListAdapter.setItemAtMap(index, "editDeviceFrameImageType", "bitmap");
                frameOnThisDeviceList.setAdapter(editDeviceFrameListAdapter);
                break;
            case "setPageImageFromBegin":
                frameLibAdapter.setItemAtMap(index, "frameImage", pageImage);
                frameLibAdapter.setItemAtMap(index, "frameImageType", "bitmap");
                frameLibAdapter.addItemAtMap(index, "iconUrl", this.URL);

                if(index==frameLibAdapter.getCount()-1){
                    frameLibLv.setAdapter(null, frameLibAdapter, null, null,null);
                }

                if(frameLibLv.getFramelibStateBeforeRefreshing()!=null)
                    frameLibLv.onRestoreInstanceState(frameLibLv.getFramelibStateBeforeRefreshing());

                break;
            case "setPageImageForMyInfo":
                myInfoAdapter.setImage(pageImage);
                myInfoLv.setAdapter(myInfoAdapter);
                break;
            case "setPageImageLinkItem":
                quicklyGetPageLinkGridViewAdapter.setImage(index, pageImage);
                imageTextGridView.setAdapter(quicklyGetPageLinkGridViewAdapter);
                isLoadingDialog.dismiss();

                imageTextGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //先把选中的view状态消除

                        for (int i = 0; i < quicklyGetPageLinkGridViewAdapter.getMaxIndex() + 1; i++) {
                            Map<String, Object> tempMap = (Map<String, Object>) quicklyGetPageLinkGridViewAdapter.getItem(i);

                            LinearLayout linkItemLinearLayout = (LinearLayout) tempMap.get("linkItemLinearLayout");
                            TextView linkItemTitleTv = (TextView) tempMap.get("linkItemTitleTv");
                            TextView linkItemUpdateTimeTv = (TextView) tempMap.get("linkItemUpdateTimeTv");
//                            ImageView linkItemPicture=(ImageView)tempMap.get("linkItemPicture");
                            TextView linkItemDigestTv = (TextView) tempMap.get("linkItemDigestTv");

                            if (i == position) {//若被选中　

                                linkItemLinearLayout.setBackgroundResource(R.drawable.quickly_get_page_link_selected_item_shape);
                                linkItemTitleTv.setTextColor(Color.WHITE);
                                linkItemUpdateTimeTv.setTextColor(Color.WHITE);
//                                linkItemPicture.setBackgroundColor(Color.WHITE);
                                linkItemDigestTv.setTextColor(Color.WHITE);
                            } else {
                                linkItemLinearLayout.setBackgroundResource(R.drawable.quickly_get_page_link_not_selected_item_shape);
                                linkItemTitleTv.setTextColor(Color.BLACK);
                                linkItemUpdateTimeTv.setTextColor(Color.BLACK);
                                linkItemDigestTv.setTextColor(Color.BLACK);
                            }
                        }


                        selectedItemIndex = position;
                        quicklyGetPageLinkGridViewAdapter.setSelectedIndex(selectedItemIndex);
                        finishPageLinkBtn.setEnabled(true);

                        if(quicklyGetPageLinkList.getQuicklyGetPageLinkListViewStateBeforeRefreshing()!=null)
                            quicklyGetPageLinkList.onRestoreInstanceState(quicklyGetPageLinkList.getQuicklyGetPageLinkListViewStateBeforeRefreshing());

                    }
                });
                finishPageLinkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = (Map<String, Object>) quicklyGetPageLinkGridViewAdapter.getItem(selectedItemIndex);
                        String linkUrl = (String) map.get("linkUrl");
                        Intent intent = new Intent();
                        intent.putExtra("pageLinkUrl", linkUrl);
                        thisQuicklyGetPageLinkFrame.setResult(QUICKLY_SET_PAGELINK_RESULT_CODE, intent);
                        thisQuicklyGetPageLinkFrame.finish();
                    }
                });
                break;
            case "setPageImageFromBeginForAddReplyPage":
                addReplyPageFrameAdapter.setItemAtMap(index, "replyPageFrameImage", pageImage);
                addReplyPageFrameAdapter.setItemAtMap(index, "replyPageFrameImageType", "bitmap");
                addReplyPagelv.setAdapter(null, null, null, addReplyPageFrameAdapter,null);
                if(addReplyPagelv.getAddReplyPageFrameStateBeforeRefreshing()!=null)
                    addReplyPagelv.onRestoreInstanceState(addReplyPagelv.getAddReplyPageFrameStateBeforeRefreshing());
                break;

        }

    }
}
