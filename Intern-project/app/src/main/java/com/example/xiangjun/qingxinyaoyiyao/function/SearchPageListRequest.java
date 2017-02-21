package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.ui.AddReplyPageFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.AddReplyPageFrameAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.DeployBatchOfDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.EditDeviceFrameListAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.FrameLibAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class SearchPageListRequest {

    private final static int ADDREPLYPAGE_RESULT_CODE = 11;

    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=page_search";
    private String token;
    private String begin;
    private String count;
    private String[] page_ids;
    private String TAG = "TIAN";
    private String type;
    private AsyncHttpClient client = new AsyncHttpClient();


    private RequestParams params = new RequestParams();
    private String result;
    private EditDeviceFrameListAdapter editDeviceFrameListAdapter;
    private ListView frameOnThisDeviceList;
    private EditDeviceFrame editDeviceFrame;

    private FrameLibAdapter frameLibAdapter;
    private RefreshableListView frameLibLv;
    private MainFrame thisMainFrame;

    private AddReplyPageFrameAdapter addReplyPageFrameAdapter;
    private RefreshableListView addReplyPagelv;
    private AddReplyPageFrame thisAddReplyPageFrame;
    private String[] relationalPagesId;
    private Button chooseBtn;
    private List<Map<String, Integer>> chosenPageIds = new ArrayList<Map<String, Integer>>();

    public EditDeviceFrameListAdapter getEditDeviceFrameListAdapter() {
        return editDeviceFrameListAdapter;
    }


    public ListView getFrameOnThisDeviceList() {
        return frameOnThisDeviceList;
    }

    public FrameLibAdapter getFrameLibAdapter() {
        return frameLibAdapter;
    }

    public RefreshableListView getFrameLibLv() {
        return frameLibLv;
    }

    public MainFrame getThisMainFrame() {
        return thisMainFrame;
    }

    public String getToken() {
        return token;
    }

    public String getBegin() {
        return begin;
    }

    public String getCount() {
        return count;
    }

    public EditDeviceFrame getEditDeviceFrame() {
        return editDeviceFrame;
    }

    public AddReplyPageFrameAdapter getAddReplyPageFrameAdapter() {
        return addReplyPageFrameAdapter;
    }

    public RefreshableListView getAddReplyPagelv() {
        return addReplyPagelv;
    }

    public AddReplyPageFrame getThisAddReplyPageFrame() {
        return thisAddReplyPageFrame;
    }

    public String[] getRelationalPagesId() {
        return relationalPagesId;
    }

    public Button getChooseBtn() {
        return chooseBtn;
    }


    //根据id读取页面的构造函数(type=1)
    public SearchPageListRequest(String token, RequestData rd, EditDeviceFrameListAdapter editDeviceFrameListAdapter,
                                 ListView frameOnThisDeviceList, EditDeviceFrame editDeviceFrame) {
        this.token = token;
        this.begin = rd.getBegin();
        this.count = rd.getBegin();
        this.page_ids = rd.getPage_ids();
        this.type = rd.getType();
        this.editDeviceFrameListAdapter = editDeviceFrameListAdapter;
        this.frameOnThisDeviceList = frameOnThisDeviceList;
        this.editDeviceFrame = editDeviceFrame;

        //指定id查询(type=1)
        this.url = url + "&token=" + this.token;

        String page_idsLinked = page_ids[0];
        for (int i = 1; i < this.page_ids.length; i++) {
            page_idsLinked = page_idsLinked + "-" + page_ids[i];
        }
        params.put("page_ids", page_idsLinked);
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (arg0 == 200) {
                    result = new String(arg2);
                    //解析result
                    //创建一个JSON对象
                    try {
                        JSONObject responseObjec = new JSONObject(result);
                        JSONArray pages = responseObjec.getJSONArray("data");

                        //动态设置listview的高度
                        final float scale = getEditDeviceFrame().getResources().getDisplayMetrics().scaledDensity;
                        int height;
                        if (pages.length() >= 3)
                            height = (int) (3 * 75 * scale);//单位sp
                        else
                            height = (int) (pages.length() * 75 * scale);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        getFrameOnThisDeviceList().setLayoutParams(params);

                        //在把页面信息加入adapter之前先把adapter清空（主要是用于在添加回复页面返回后对这个adapter的改写，因为之前已经有页面了）
                        if(getEditDeviceFrameListAdapter().getCount()>0)
                            getEditDeviceFrameListAdapter().clearAllItem();

                        for (int i = 0; i < pages.length(); i++) {

                            String title = pages.getJSONObject(i).getString("title");//主标题
                            String description = pages.getJSONObject(i).getString("description");//副标题

                            Map<String, Object> editDeviceFrameLibMap = new HashMap<String, Object>();

                            //添加进list里
                            editDeviceFrameLibMap.put("editDeviceFrameImage", R.drawable.logo);
                            editDeviceFrameLibMap.put("editDeviceFrameImageType", "drawable");
                            editDeviceFrameLibMap.put("editDeviceFrameName", title);
                            editDeviceFrameLibMap.put("editDeviceFrameHint", description);
                            editDeviceFrameLibMap.put("editDeviceFrameId", page_ids[i]);


                            getEditDeviceFrameListAdapter().addItem(editDeviceFrameLibMap);

                        }

                        getFrameOnThisDeviceList().setAdapter(getEditDeviceFrameListAdapter());

                        for (int i = 0; i < pages.length(); i++) {

                            String icon_url = pages.getJSONObject(i).getString("icon_url");//图标
                            GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(icon_url,
                                    i, getEditDeviceFrameListAdapter(), getFrameOnThisDeviceList());
                            getImageAsyncTask.execute();
                        }


                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getEditDeviceFrame());
                        builder.setTitle("提示");
                        builder.setMessage("JSON包解析出错，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getEditDeviceFrame());
                    builder.setTitle("提示");
                    builder.setMessage("网络异常，请检查！");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }
        });


    }
    //这是从头获取页面的构造函数(type=2)
    public SearchPageListRequest(String token, RequestData rd, final FrameLibAdapter frameLibAdapter,
                                 RefreshableListView frameLibLv, MainFrame thisMainFrame) {
        this.token = token;
        this.begin = rd.getBegin();
        this.count = rd.getCount();
        this.page_ids = rd.getPage_ids();
        this.type = rd.getType();
        this.frameLibAdapter = frameLibAdapter;
        this.frameLibLv = frameLibLv;
        this.thisMainFrame = thisMainFrame;


        //从头查询(type=2)
        this.url = url + "&token=" + this.token;

        params.put("type", this.type);
        params.put("begin", this.begin);
        params.put("count", this.count);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                if (arg0 == 200) {

                    result = new String(arg2);
                    //解析result
                    //创建一个JSON对象
                    try {
                        JSONObject responseObjec = new JSONObject(result);
                        int err_code=responseObjec.getInt("err_code");
                        if(err_code==0){
                            JSONArray pages = responseObjec.getJSONArray("data");

                            if(Integer.valueOf(begin)+Integer.valueOf(count)>=pages.length())
                                frameLibAdapter.setHasFinishedLoading(true);

                            for (int i = 0; i < pages.length(); i++) {

                                String comment = pages.getJSONObject(i).getString("comment");//页面名称
                                String title = pages.getJSONObject(i).getString("title");//主标题
                                String description = pages.getJSONObject(i).getString("description");//副标题
                                String page_id = pages.getJSONObject(i).getString("page_id");
                                String page_url = pages.getJSONObject(i).getString("page_url");
                                Map<String, Object> frameLibMap = new HashMap<String, Object>();

                                //添加进list里
                                frameLibMap.put("frameImage", R.drawable.logo);
                                frameLibMap.put("frameImageType", "drawable");
                                frameLibMap.put("frameName", title);//主标题
                                frameLibMap.put("frameHint", description);
                                frameLibMap.put("frameId", page_id);
                                frameLibMap.put("pageName", comment);//页面名称，编辑设备的时候用
                                frameLibMap.put("pageLink", page_url);//页面链接，编辑设备的时候用

                                getFrameLibAdapter().addItem(frameLibMap);

                            }
                            getFrameLibLv().setAdapter(null, getFrameLibAdapter(), null, null,null);

                            for (int i = 0; i < pages.length(); i++) {
                                String icon_url = pages.getJSONObject(i).getString("icon_url");//图标

                                GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(icon_url, i, getFrameLibAdapter(), getFrameLibLv());
                                getImageAsyncTask.execute();
                            }

                            getFrameLibAdapter().setLastLoadingMaxIndex(Integer.valueOf(getBegin())+Integer.valueOf(getCount())-1);

                            getFrameLibLv().setonRefreshListener(new RefreshableListView.OnRefreshListener() {

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
                                            getFrameLibLv().setFramelibStateBeforeRefreshing(getFrameLibLv().onSaveInstanceState());//用于防止更新完后自动跳回顶部
                                            getFrameLibAdapter().notifyDataSetChanged();
                                            getFrameLibLv().setOnRefreshFrameLibListCompleteParams(getToken(), getFrameLibLv(),
                                                    getThisMainFrame());
                                            getFrameLibLv().onRefreshComplete();

                                        }
                                    }.execute(null, null, null);
                                }
                            });
                        }else{
                            String err_msg=responseObjec.getString("err_msg");
                            AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());
                            builder.setTitle("提示");
                            builder.setMessage("在加载页面列表时发生了未知错误\n" +
                                    "错误代码：" + err_code + "\n" +
                                    "错误信息：" + err_msg + "\n" +
                                    "请下拉重试或登录氢心官网申请支持");
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                        builder.setTitle("提示");
                        builder.setMessage("在加载页面列表时JSON包解析出错，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }


                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                    builder.setTitle("提示");
                    builder.setMessage("在加载页面列表时网络异常，请检查！");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }
        });

    }

    //这是在添加回复页面的获取全部页面的构造函数
    public SearchPageListRequest(String token, RequestData rd, AddReplyPageFrameAdapter addReplyPageFrameAdapter,
                                 RefreshableListView addReplyPagelv, final AddReplyPageFrame thisAddReplyPageFrame,
                                 final String[] relationalPagesId, Button chooseBtn) {
        this.token = token;
        this.begin = rd.getBegin();
        this.count = rd.getCount();
        this.page_ids = rd.getPage_ids();
        this.type = rd.getType();
        this.addReplyPageFrameAdapter = addReplyPageFrameAdapter;
        this.addReplyPagelv = addReplyPagelv;
        this.thisAddReplyPageFrame = thisAddReplyPageFrame;
        this.relationalPagesId = relationalPagesId;
        this.chooseBtn = chooseBtn;

        //从头查询(type=2)
        this.url = url + "&token=" + this.token;

        params.put("type", this.type);
        params.put("begin", this.begin);
        params.put("count", this.count);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {

            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                if (arg0 == 200) {

                    result = new String(arg2);
                    //解析result
                    //创建一个JSON对象
                    try {
                        JSONObject responseObjec = new JSONObject(result);
                        JSONArray pages = responseObjec.getJSONArray("data");

                        if(Integer.valueOf(begin)+Integer.valueOf(count)>=pages.length())
                            getAddReplyPageFrameAdapter().setHasFinishedLoading(true);

                        for (int i = 0; i < pages.length(); i++) {

                            String comment = pages.getJSONObject(i).getString("comment");//页面名称
                            String title = pages.getJSONObject(i).getString("title");//主标题
                            String description = pages.getJSONObject(i).getString("description");//副标题
                            String page_id = pages.getJSONObject(i).getString("page_id");
                            String page_url = pages.getJSONObject(i).getString("page_url");
                            Map<String, Object> addReplayPageMap = new HashMap<String, Object>();

                            //添加进list里
                            addReplayPageMap.put("replyPageFrameImage", R.drawable.logo);
                            addReplayPageMap.put("replyPageFrameImageType", "drawable");
                            addReplayPageMap.put("replyPageFrameName", title);//主标题
                            addReplayPageMap.put("replyPageFrameHint", description);
                            addReplayPageMap.put("addReplyPageChooseFlag", R.drawable.choose_flag);
                            addReplayPageMap.put("replyPageFrameId", page_id);
                            addReplayPageMap.put("replyPage_PageName", comment);//页面名称，编辑设备的时候用
                            addReplayPageMap.put("replyPage_PageLink", page_url);//页面链接，编辑设备的时候用

                            getAddReplyPageFrameAdapter().addItem(addReplayPageMap);

                        }
                        getAddReplyPagelv().setAdapter(null, null, null, getAddReplyPageFrameAdapter(),null);

                        for (int i = 0; i < pages.length(); i++) {
                            String icon_url = pages.getJSONObject(i).getString("icon_url");//图标

                            GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(icon_url, i, getAddReplyPageFrameAdapter(), getAddReplyPagelv());
                            getImageAsyncTask.execute();
                        }

                        getAddReplyPagelv().setOnRefreshAddReplyPageListCompleteParams(getToken(), getAddReplyPagelv(), getThisAddReplyPageFrame(),
                                relationalPagesId, getChooseBtn());

                        getAddReplyPageFrameAdapter().setLastLoadingMaxIndex(Integer.valueOf(getBegin())+Integer.valueOf(getCount())-1);


                        getAddReplyPagelv().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HashMap<String, Object> selectedItemMap =
                                        (HashMap) getAddReplyPageFrameAdapter().getItem(position - 1);

                                String selectedItemPageId = (String) selectedItemMap.get("replyPageFrameId");
                                boolean haveFoundPageId = false;
                                for (int i = 0; i < relationalPagesId.length; i++) {
                                    if (relationalPagesId[i].equals(selectedItemPageId)) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(thisAddReplyPageFrame);  //先得到构造器
                                        builder.setTitle("提示"); //设置标题
                                        builder.setMessage("该页面已关联至该设备"); //设置内容

                                        //为了保证按钮的顺序一样，所以只能反过来设置
                                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        //参数都设置完成了，创建并显示出来
                                        builder.create().show();
                                        haveFoundPageId = true;
                                        break;
                                    }
                                }
                                if (!haveFoundPageId) {

                                    ImageView selectedItemImage = (ImageView) selectedItemMap.get("addReplyPageChooseFlagView");
                                    selectedItemImage.clearAnimation();

                                    if (selectedItemImage.getVisibility() == View.GONE) {
                                        selectedItemImage.setVisibility(View.VISIBLE);
                                        getAddReplyPageFrameAdapter().addInSelectedList(position-1);
                                        Map<String, Integer> chosenPageMap = new HashMap<String, Integer>();
                                        chosenPageMap.put((String) selectedItemMap.get("replyPageFrameId"), position - 1);
                                        chosenPageIds.add(chosenPageMap);
                                    } else {
                                        selectedItemImage.setVisibility(View.GONE);
                                        getAddReplyPageFrameAdapter().deleteSelectedIndex(position-1);
                                        for (int i = 0; i < chosenPageIds.size(); i++) {
                                            String selectedId = (String) selectedItemMap.get("replyPageFrameId");
                                            Integer selectedIndex = chosenPageIds.get(i).get(selectedId);
                                            if (selectedIndex != null) {
                                                if (selectedIndex.intValue() == position - 1) {
                                                    chosenPageIds.remove(i);
                                                }
                                            }

                                        }

                                    }

                                    parent.requestLayout();

                                    if (!chosenPageIds.isEmpty())
                                        getChooseBtn().setEnabled(true);
                                }

                                getChooseBtn().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int page_idsLength;
                                        if(relationalPagesId.length==1 && relationalPagesId[0].equals(""))//如果是原本没有相关页面的话
                                            page_idsLength=chosenPageIds.size();
                                        else
                                            page_idsLength=chosenPageIds.size()+relationalPagesId.length;
                                        String[] page_ids = new String[page_idsLength];

                                        int pageIdsIndex=0;
                                        //把新选的和原来的拼接起来
                                        for (int i = 0; i < chosenPageIds.size(); i++) {
                                            Set<String> page_idSet=chosenPageIds.get(i).keySet();
                                            Iterator<String> page_idIterator=page_idSet.iterator();
                                            while(page_idIterator.hasNext())
                                                page_ids[pageIdsIndex++]=page_idIterator.next();
                                        }
                                        //之所以采用page_idsLength-chosenPageIds.size()来判断是为了防止原本没有页面这种情况（这种情况下relationalPagesId.length仍为1）
                                        for (int i = 0; i < page_idsLength-chosenPageIds.size(); i++) {
                                            page_ids[pageIdsIndex]=relationalPagesId[i];
                                            pageIdsIndex++;
                                        }
                                        Intent intent = new Intent();
                                        intent.putExtra("page_idsArray", page_ids);
                                        getThisAddReplyPageFrame().setResult(ADDREPLYPAGE_RESULT_CODE,intent);
                                        getThisAddReplyPageFrame().finish();
                                    }
                                });


                            }
                        });


                        getAddReplyPagelv().setonRefreshListener(new RefreshableListView.OnRefreshListener() {

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
                                        getAddReplyPagelv().setAddReplyPageFrameStateBeforeRefreshing(getAddReplyPagelv().onSaveInstanceState());
                                        getAddReplyPagelv().setOnRefreshAddReplyPageListCompleteParams(getToken(), getAddReplyPagelv(), getThisAddReplyPageFrame(),
                                                relationalPagesId, getChooseBtn());
                                        getAddReplyPageFrameAdapter().notifyDataSetChanged();
                                        getAddReplyPagelv().onRefreshComplete();
                                    }
                                }.execute(null, null, null);
                            }
                        });


                    } catch (JSONException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getThisAddReplyPageFrame());
                        builder.setTitle("提示");
                        builder.setMessage("JSON包解析出错，请联系开发人员！");
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }


                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisAddReplyPageFrame());
                    builder.setTitle("提示");
                    builder.setMessage("网络异常，请检查！");
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }
        });


    }
}
