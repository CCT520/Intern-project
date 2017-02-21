package com.example.xiangjun.qingxinyaoyiyao.function;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.xiangjun.qingxinyaoyiyao.R;
import com.example.xiangjun.qingxinyaoyiyao.ui.ChooseAccountAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.ChooseAccountFrame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangjun on 16/1/10.
 */
public class GetOfficialAccountListAsyncTask extends AsyncTask<Void, String, Void> {

    private String response;
    private int[] accountType;
    private int[] verify;
    private int[] industry;
    private int[] officialAccountId;
    private String[] officialAccountName;
    private String[] officialAccountHint;
    private String[] affliatedCareer;
    private boolean[] isAuthorized;//判断是否已授权
    private Bitmap[] head_image;
    private String[] headImageURL;
    private ChooseAccountFrame thisChooseAccountFrame;
    private List<Map<String, Object>> mData;
    private String token;
    private String accountName;
    private  ListView accountListView;
    private ChooseAccountAdapter adapter;

    public GetOfficialAccountListAsyncTask(String response, ChooseAccountFrame thisChooseAccountFrame,
                                           List<Map<String, Object>> mData, String token, String accountName) {
        this.response = response;
        this.thisChooseAccountFrame = thisChooseAccountFrame;
        this.mData = mData;
        this.token = token;
        this.accountName = accountName;
    }

    @Override
    protected Void doInBackground(Void... params) {



        //创建一个JSON对象
        try {
            JSONObject responseObjec= new JSONObject(response.toString());
            JSONObject data=responseObjec.getJSONObject("data");
            JSONArray accountList=data.getJSONArray("account_list");

            accountType=new int[accountList.length()];
            verify=new int[accountList.length()];
            industry=new int[accountList.length()];
            officialAccountId=new int[accountList.length()];
            officialAccountName=new String[accountList.length()];
            officialAccountHint=new String[accountList.length()];
            affliatedCareer=new String[accountList.length()];
            isAuthorized=new boolean[accountList.length()];//判断是否已授权
            head_image=new Bitmap[accountList.length()];
            headImageURL=new String[accountList.length()];


            for(int i=0;i<accountList.length();i++){
                JSONObject officialAccountInfo=accountList.getJSONObject(i);
                accountType[i]=officialAccountInfo.getInt("type");
                verify[i]=officialAccountInfo.getInt("verify");
                industry[i]=officialAccountInfo.getInt("industry");
                officialAccountName[i]=officialAccountInfo.getString("name");
                isAuthorized[i]=false;//默认是没授权
                JSONArray componentList=officialAccountInfo.getJSONArray("component_list");

                //遍历componentlist，若有一个component_appid等于"wxf730bd17503fbfa9"，则已授权
                for(int j=0;j<componentList.length();j++){
                    JSONObject componentListObject=componentList.getJSONObject(j);
                    String component_appid=componentListObject.getString("component_appid");
                    if(component_appid.equals("wxf730bd17503fbfa9")){
                        isAuthorized[i]=true;
                        break;
                    }
                }
                officialAccountHint[i]=getOffcialAccountHint(accountType[i], verify[i]);
                affliatedCareer[i]=getAffliatedCareer(industry[i]);//所属行业

                //获取头像
                JSONObject auth_ori_info=officialAccountInfo.getJSONObject("auth_ori_info");
                JSONObject authorizer_info=auth_ori_info.getJSONObject("authorizer_info");
                headImageURL[i]=authorizer_info.getString("head_img");
                head_image[i]=getImageBitmap(headImageURL[i]);

                //获取每个账号的id值
                officialAccountId[i]=officialAccountInfo.getInt("id");
            }

            //将自定义的列表内容放入list容器中
            mData = getData();
            //自定义的适配器
            adapter = new ChooseAccountAdapter(thisChooseAccountFrame,mData,token,accountName);

            //定义一个listview并设置适配器
            accountListView = (ListView) thisChooseAccountFrame.findViewById(R.id.accountList_view);

            publishProgress("setAdapter");


        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    //自定义的list数据
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < officialAccountName.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("officialAccountName", officialAccountName[i]);
            map.put("officialAccountHint", officialAccountHint[i]);
            map.put("officialAccountIsAuthorized", isAuthorized[i]);
            map.put("officialAccountImage", head_image[i]);
            map.put("officialAccountImageURL", headImageURL[i]);
            map.put("officialAccountAffliatedCareer", affliatedCareer[i]);
            map.put("officialAccountId", officialAccountId[i]);
            list.add(map);
        }

        return list;
    }

    public String getOffcialAccountHint(int accountType,int verify){
        if(accountType==1){
            if(verify==1){
                return "认证订阅号";
            }else {
                return "未认证订阅号";
            }
        }else {
            if(verify==1){
                return "认证服务号";
            }else {
                return "未认证服务号";
            }
        }
    }

    public String getAffliatedCareer(int industry){
        switch (industry){
            case 10010:
                return "互联网";
        }
        return null;
    }

    /**
     * 从指定URL获取图片
     * @param url
     * @return
     */
    private Bitmap getImageBitmap(String url){
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onProgressUpdate(String... progress) {

        if(progress[0].equals("setAdapter")){
            accountListView.setAdapter(adapter);
        }
    }
}
