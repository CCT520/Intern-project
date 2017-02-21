package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.security.KeyChain;
import android.widget.Toast;
import com.example.xiangjun.qingxinyaoyiyao.ui.ChooseAccountFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.LoginFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.Splash;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
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
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * Created by xiangjun on 15/12/2.
 */
public class LoginAsyncTask extends AsyncTask<Void, String, Void> {

    private String accountName;
    private String accountNameType;
    private String accountPwd;

    private LoginFrame thisFrame;//用于toast

    private AlertDialog.Builder builder;
    private AlertDialog isLogginginDialog;

    private String response;


    public LoginAsyncTask(String accountName, String accountPwd, LoginFrame thisFrame, AlertDialog isLogginginDialog){
        this.accountName=accountName;
        this.accountPwd=accountPwd;
        this.thisFrame=thisFrame;
        this.isLogginginDialog=isLogginginDialog;
        this.accountNameType="";
    }




    @Override
    protected Void doInBackground(Void... params) {

        Pattern phoneNumberPattern=Pattern.compile("^(\\d{11})$");
        Matcher phoneNumberMatcher=phoneNumberPattern.matcher(accountName);

        Pattern eMailPattern=Pattern.compile("^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
        Matcher eMailMatcher=eMailPattern.matcher(accountName);

        if(phoneNumberMatcher.matches())
            accountNameType="mobile";
        else if(eMailMatcher.matches())
            accountNameType="email";
        else{
            publishProgress("errorAccountNameStyle");
            return null;
        }


        login();

        return null;
    }


    public String login(){

        if(accountNameType.equals(""))
            return "accountNameTypeError";

        //第一步：创建HttpClient对象
        HttpClient httpClient=new DefaultHttpClient();
        //第二步：创建代表请求的对象,参数是访问的服务器地址
        HttpGet httpGet=new HttpGet("http://api.wxyaoyao.com/3/"+"user/get_token?token=no_token"+
                "&"+accountNameType+"="+accountName+"&password="+accountPwd);

        //请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);

        //读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

        try {
            //第三步：执行请求，获取服务器发还的相应对象
            HttpResponse httpResponse=httpClient.execute(httpGet);
            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if(httpResponse.getStatusLine().getStatusCode()==200){
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity entity=httpResponse.getEntity();
                response= EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
                //创建一个JSON对象
                JSONObject responseObjec= new JSONObject(response.toString());
                //获取err_code
                 int err_code= responseObjec.getInt("err_code");
                String err_msg=responseObjec.getString("err_msg");

                if(err_code!=0) {//验证不成功
                    if(err_msg.equals("PERMISSION_ERROR")){//用户名或者密码错误
                        publishProgress("accountOrPwdError");
                        return "failure";
                    }
                }else {//验证成功

                    //把登录名缓存以便下次登录
                    SharedPreferences loginNameCachePreferences= thisFrame.getSharedPreferences("loginNameCache",
                            Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginNameCachePreferences.edit();
                    Boolean loginNameIsCached = loginNameCachePreferences.getBoolean("loginNameIsCached", false);

                    if(loginNameIsCached==false){//若没缓存则直接缓存

                        editor.putBoolean("loginNameIsCached", true);
                        editor.putString("latestLoginName", accountName);
                        editor.commit();
                    }else{//若已缓存，则检查这次登录名与上次是否相同，若不同则更新缓存
                        String latestLoginName=loginNameCachePreferences.getString("latestLoginName",accountName);
                        if(!latestLoginName.equals(accountName)){
                            editor.remove("latestLoginName");
                            editor.putString("latestLoginName", accountName);
                            editor.commit();
                        }
                    }

                    //验证是否只有一个公众号，以及是否已绑定
                    JSONObject data=responseObjec.getJSONObject("data");
                    JSONArray accountList=data.getJSONArray("account_list");
                    String token=data.getString("token");


                    if(accountList.length()==0){//没有公众号
                        publishProgress("noOfficialAccount");
                        return "failure";
                    }else{
                        int[] accountType=new int[accountList.length()];
                        int[] verify=new int[accountList.length()];
                        int[] industry=new int[accountList.length()];
                        int[] officialAccountId=new int[accountList.length()];
                        String[] officialAccountName=new String[accountList.length()];
                        String[] officialAccountHint=new String[accountList.length()];
                        String[] affliatedCareer=new String[accountList.length()];
                        boolean[] isAuthorized=new boolean[accountList.length()];//判断是否已授权
                        Bitmap[] head_image=new Bitmap[accountList.length()];
                        String[] headImageURL=new String[accountList.length()];
                        byte[][] headImageBytes=new byte[accountList.length()][200000];

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

                            //把获得的bitmap对象转换成byte数组以便传递
                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
                            head_image[i].compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] bitmapBytes=baos.toByteArray();
                            for(int j=0;j<bitmapBytes.length;j++){
                                headImageBytes[i][j]=bitmapBytes[j];
                            }

                            //获取每个账号的id值
                            officialAccountId[i]=officialAccountInfo.getInt("id");
                        }

                        if(accountList.length()==1){//只有一个公众号
                            int accountId=data.getInt("account_id");

                            if(accountId!=0){//已绑定，则直接跳到主界面
                                Date date=new Date();

                                SharedPreferences mySharedPreferences= thisFrame.getSharedPreferences("userInfoCache",
                                        Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = mySharedPreferences.edit();
                                editor2.putBoolean("isCached", true);
                                editor2.putString("cachedDate", String.valueOf(date.getTime()));
                                editor2.putString("chosenOfficialAccountName", officialAccountName[0]);
                                editor2.putString("chosenOfficialAccountHint", officialAccountHint[0]);
                                editor2.putString("accountName", accountName);
                                editor2.putString("chosenOfficialAccountAffliatedCareer", affliatedCareer[0]);
                                editor2.putString("chosenOfficialAccountHeadImageURl", headImageURL[0]);
                                editor2.putString("token", token);
                                editor2.commit();

                                Intent intent = new Intent();
                                intent.putExtra("chosenOfficialAccountName", officialAccountName[0]);
                                intent.putExtra("chosenOfficialAccountHint", officialAccountHint[0]);
                                intent.putExtra("accountName", accountName);
                                intent.putExtra("chosenOfficialAccountAffliatedCareer", affliatedCareer[0]);
                                intent.putExtra("imageIsURL", true);
                                intent.putExtra("chosenOfficialAccountHeadImageURl", headImageURL[0]);
                                intent.putExtra("token", token);

                                intent.setClass(thisFrame, MainFrame.class);
                                isLogginginDialog.dismiss();

                                thisFrame.startActivity(intent);
                                thisFrame.finish();
                            }else {//未绑定，跳到绑定页面让用户绑定
                                Intent intent = new Intent();
                                intent.putExtra("response",response);
                                intent.putExtra("accountName", accountName);//登陆名（账号
                                intent.putExtra("token", token);

                                intent.setClass(thisFrame, ChooseAccountFrame.class);

                                isLogginginDialog.dismiss();
                                thisFrame.startActivity(intent);
                                thisFrame.finish();
                            }
                        }else {//有多个公众号，则跳到绑定页面，让用户绑定
                            Intent intent = new Intent();
                            intent.putExtra("response",response);
                            intent.putExtra("accountName",accountName);//登陆名（账号）
                            intent.putExtra("token",token);
                            intent.setClass(thisFrame, ChooseAccountFrame.class);

                            isLogginginDialog.dismiss();
                            thisFrame.startActivity(intent);
                            thisFrame.finish();
                        }
                    }

                }
            }else {
                publishProgress("requestCodeIsNotValid");
                return "failure";
            }


        } catch (ConnectTimeoutException e) {
            publishProgress("requestTimeOut");
        } catch (IOException e) {
            publishProgress("IOException");
        } catch (JSONException e) {
            publishProgress("JSONException");
        }



        return "success";
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

        switch (progress[0]){
            case "errorAccountNameStyle":
                isLogginginDialog.dismiss();
                builder=new AlertDialog.Builder(thisFrame);
                builder.setTitle("警告");
                builder.setMessage("用户名不合法！");
                builder.setCancelable(false);
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "accountOrPwdError":
                isLogginginDialog.dismiss();
                builder=new AlertDialog.Builder(thisFrame);
                builder.setTitle("警告");
                builder.setMessage("用户名或密码错误！");
                builder.setCancelable(false);
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "noOfficialAccount":
                isLogginginDialog.dismiss();
                builder=new AlertDialog.Builder(thisFrame);
                builder.setTitle("警告");
                builder.setMessage("对不起，您还没有授权该账户使用您的公众号！");
                builder.setCancelable(false);
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "requestCodeIsNotValid":
                isLogginginDialog.dismiss();
                builder=new AlertDialog.Builder(thisFrame);
                builder.setTitle("警告");
                builder.setMessage("网络异常，请检查网络！");
                builder.setCancelable(false);
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "requestTimeOut":
                builder.setTitle("提示");
                builder.setMessage("请求超时，请检查网络！");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "IOException":
                builder.setTitle("提示");
                builder.setMessage("出现IO异常，请联系开发人员！");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "JSONException":
                builder.setTitle("提示");
                builder.setMessage("JSON包解析出错，请联系开发人员！");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
    }
}
