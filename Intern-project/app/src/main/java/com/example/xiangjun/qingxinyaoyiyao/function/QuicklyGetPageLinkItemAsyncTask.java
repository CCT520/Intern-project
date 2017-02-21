package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.GridView;

import com.example.xiangjun.qingxinyaoyiyao.ui.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * Created by xiangjun on 15/12/24.
 */
public class QuicklyGetPageLinkItemAsyncTask extends AsyncTask<Void, String, Void> {

    private String URL;
    private GridView imageTextGridView;
    private RefreshableListView quicklyGetPageLinkList;
    private QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter;
    private Button finishPageLinkBtn;
    private QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame;
    private String start_key;
    private String count;
    private AlertDialog isLoadingDialog;

    public QuicklyGetPageLinkItemAsyncTask(String URL, RefreshableListView quicklyGetPageLinkList,GridView imageTextGridView,
                                           QuicklyGetPageLinkGridViewAdapter quicklyGetPageLinkGridViewAdapter,
                                           Button finishPageLinkBtn,QuicklyGetPageLinkFrame thisQuicklyGetPageLinkFrame,
                                           String start_key,String count) {
        this.URL = URL;
        this.quicklyGetPageLinkList=quicklyGetPageLinkList;
        this.imageTextGridView = imageTextGridView;
        this.quicklyGetPageLinkGridViewAdapter = quicklyGetPageLinkGridViewAdapter;
        this.finishPageLinkBtn=finishPageLinkBtn;
        this.thisQuicklyGetPageLinkFrame=thisQuicklyGetPageLinkFrame;
        this.start_key=start_key;
        this.count=count;

    }

    @Override
    protected Void doInBackground(Void... params) {

        publishProgress("showLoadingDialog");

        HttpClient httpClient=new DefaultHttpClient();

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("start_key", start_key));
        requestParams.add(new BasicNameValuePair("count", count));

        try {
            HttpPost request = new HttpPost(URL);
            HttpResponse httpResponse;
            HttpEntity entity = new UrlEncodedFormEntity(requestParams, "UTF-8");
            request.setEntity(entity);
            //请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);

            //读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

            httpResponse = httpClient.execute(request);

            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity responseEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(responseEntity, "utf-8");//将entity当中的数据转换为字符串
                //创建一个JSON对象
                JSONObject responseObjec = new JSONObject(response.toString());
                JSONObject data=responseObjec.getJSONObject("data");

                String lastKey=data.getString("last_key");
                if(lastKey.equals(""))//已经加载完
                    quicklyGetPageLinkGridViewAdapter.setHasFinishedLoading(true);

                JSONArray list=data.getJSONArray("list");
                for(int i=list.length()-1;i>=0;i--){
                    JSONObject listObject=list.getJSONObject(i);
                    long updateTime=listObject.getLong("update_time");
                    Date date=new Date(updateTime*1000);
                    DateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日");
                    String formattedDate=df1.format(date);

                    JSONArray linkDetail=listObject.getJSONArray("news_item");
                    JSONObject linkDetailObject=linkDetail.getJSONObject(0);
                    String title=linkDetailObject.getString("title");
                    String pitureUrl=linkDetailObject.getString("thumb_media_url");
                    String digest=linkDetailObject.getString("digest");
                    String linkUrl=linkDetailObject.getString("url");

                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("linkItemTitle",title);
                    map.put("linkItemUpdateTime",formattedDate);
                    map.put("linkItemPicture",null);
                    map.put("linkItemDigest", digest);
                    map.put("linkUrl",linkUrl);

                    quicklyGetPageLinkGridViewAdapter.addItem(map);

                    GetImageAsyncTask getImageAsyncTask=new GetImageAsyncTask(pitureUrl,i,quicklyGetPageLinkList,imageTextGridView,
                            quicklyGetPageLinkGridViewAdapter,finishPageLinkBtn,thisQuicklyGetPageLinkFrame,isLoadingDialog);
                    getImageAsyncTask.execute();
                }

                quicklyGetPageLinkList.setOnRefreshQuicklyGetPageLinkListCompleteParams(URL,quicklyGetPageLinkList,
                        imageTextGridView, quicklyGetPageLinkGridViewAdapter,finishPageLinkBtn,thisQuicklyGetPageLinkFrame,lastKey,"20");
            }else {
                publishProgress("networkInnormal");


            }
        } catch (ConnectTimeoutException e) {
            publishProgress("requestTimeOut");
        }catch (JSONException e) {
            publishProgress("JSONException");
        } catch (ClientProtocolException e) {
            publishProgress("ClientProtocolException");
        } catch (IOException e) {
            publishProgress("IOException");
        }


        return null;
    }

    protected void onProgressUpdate(String... progress) {

        if(progress[0].equals("showLoadingDialog")){
            AlertDialog.Builder builder=new AlertDialog.Builder(thisQuicklyGetPageLinkFrame);
            builder.setMessage("正在加载，请稍候...");
            builder.setTitle("提示");
            builder.setCancelable(false);
            isLoadingDialog=builder.create();
            isLoadingDialog.show();
        }else if(progress[0].equals("networkInnormal")){
            isLoadingDialog.dismiss();
            AlertDialog.Builder builder=new AlertDialog.Builder(thisQuicklyGetPageLinkFrame);
            builder.setMessage("网络异常，请检查！");
            builder.setTitle("提示");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else if(progress[0].equals("invalidParam")){
            isLoadingDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisQuicklyGetPageLinkFrame);
            builder.setTitle("提示");
            builder.setMessage("参数错误，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else if(progress[0].equals("ClientProtocolException")){
            isLoadingDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisQuicklyGetPageLinkFrame);
            builder.setTitle("提示");
            builder.setMessage("出现ClientProtocol异常，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else if(progress[0].equals("IOException")){
            isLoadingDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisQuicklyGetPageLinkFrame);
            builder.setTitle("提示");
            builder.setMessage("出现IO异常，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else if(progress[0].equals("JSONException")){
            isLoadingDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisQuicklyGetPageLinkFrame);
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


    }
}
