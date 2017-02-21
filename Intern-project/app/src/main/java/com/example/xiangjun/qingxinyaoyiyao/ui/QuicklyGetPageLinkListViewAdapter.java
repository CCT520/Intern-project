package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.function.QuicklyGetPageLinkItemAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangjun on 16/1/13.
 */
public class QuicklyGetPageLinkListViewAdapter extends BaseAdapter {


    private List<Map<String, Object>> list;
    private QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    private QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter;
    private List<Map<String, Object>> quicklyGetPageLinkGridViewList;
    private RefreshableListView quicklyGetPageLinkList;
    private String URL;

    private ActionBar bar;
    private Button finishPageLinkBtn;
    private String token;

    private QuicklyGetPageLinkListViewAdapter thisQuicklyGetPageLinkListViewAdapter;

    public QuicklyGetPageLinkListViewAdapter(List<Map<String, Object>> list, QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame,
                                             String token,RefreshableListView quicklyGetPageLinkList) {
        this.list = list;
        this.thisQuicklyGetPageLinkFrame = thisQuicklyGetPageLinkFrame;
        this.mInflater = LayoutInflater.from(thisQuicklyGetPageLinkFrame);
        this.token=token;
        this.quicklyGetPageLinkList=quicklyGetPageLinkList;
        thisQuicklyGetPageLinkListViewAdapter=this;
    }



    public void addItem(Map<String, Object> map) {
        list.add(0, map);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            //初始化holder的各个变量
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.quickly_get_page_link_listview_item, null);
            holder.quicklyGetPageLinkGridView=(GridView) convertView.findViewById(R.id.imageTextGridView);
            convertView.setTag(holder);


        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        quicklyGetPageLinkGridViewList = new ArrayList<Map<String, Object>>();
        quicklyGetPageLinkGridViewAdapter =new QuicklyGetPageLinkGridViewAdapter(quicklyGetPageLinkGridViewList,thisQuicklyGetPageLinkFrame);
        //自定义actionbar
        bar=thisQuicklyGetPageLinkFrame.getActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar_layout_quickly_get_page_link);
        TextView addPageTitle=(TextView)bar.getCustomView().findViewById(R.id.getPageLinkTitle);
        addPageTitle.setText("图文消息列表");
        finishPageLinkBtn=(Button)bar.getCustomView().findViewById(R.id.finishPageLinkBtn);

        URL="http://api.wxyaoyao.com/3/addon/api?name=material&action=api_list&token="+token;
        QuicklyGetPageLinkItemAsyncTask quicklyGetPageLinkItemAsyncTask=new QuicklyGetPageLinkItemAsyncTask(URL,quicklyGetPageLinkList,
                holder.quicklyGetPageLinkGridView, quicklyGetPageLinkGridViewAdapter,finishPageLinkBtn,thisQuicklyGetPageLinkFrame,"","20");
        quicklyGetPageLinkItemAsyncTask.execute();

        quicklyGetPageLinkList.setonRefreshListener(new RefreshableListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        quicklyGetPageLinkList.setQuicklyGetPageLinkListViewStateBeforeRefreshing(quicklyGetPageLinkList.onSaveInstanceState());
                        quicklyGetPageLinkList.setOnRefreshQuicklyGetPageLinkListCompleteParams(URL,quicklyGetPageLinkList,
                                holder.quicklyGetPageLinkGridView, quicklyGetPageLinkGridViewAdapter,finishPageLinkBtn,thisQuicklyGetPageLinkFrame,"","20");
                        thisQuicklyGetPageLinkListViewAdapter.notifyDataSetChanged();
                        quicklyGetPageLinkList.onRefreshComplete();
                    }
                }.execute(null, null, null);
            }
        });

        return convertView;
    }

    //自定义holder来装按钮与其他组件
    public final class ViewHolder {
        public GridView quicklyGetPageLinkGridView;
    }
}
