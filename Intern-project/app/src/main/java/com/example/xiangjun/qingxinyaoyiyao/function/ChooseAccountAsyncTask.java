package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.ui.*;

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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * Created by xiangjun on 15/12/7.
 */
public class ChooseAccountAsyncTask extends AsyncTask<Void, String, Void> {

    private ChooseAccountFrame thisFrame;
    private String URL;
    private String chosenOfficialAccount;
    private String chosenOfficialAccountHint;
    private String chosenOfficialAccountAffliatedCareer;
    private String accountName;
    private String token;
    private String chosenOfficialAccountHeadImageURl;
    private AlertDialog isEnteringMainFrameDialog;

    public ChooseAccountAsyncTask(ChooseAccountFrame thisFrame, String URL, String token,String chosenOfficialAccount,
                                  String chosenOfficialAccountHint, String chosenOfficialAccountAffliatedCareer,
                                  String accountName, String chosenOfficialAccountHeadImageURl,AlertDialog isEnteringMainFrameDialog) {
        this.thisFrame = thisFrame;
        this.URL = URL;
        this.token=token;
        this.chosenOfficialAccount = chosenOfficialAccount;
        this.chosenOfficialAccountHint = chosenOfficialAccountHint;
        this.chosenOfficialAccountAffliatedCareer = chosenOfficialAccountAffliatedCareer;
        this.accountName = accountName;
        this.chosenOfficialAccountHeadImageURl=chosenOfficialAccountHeadImageURl;
        this.isEnteringMainFrameDialog=isEnteringMainFrameDialog;
    }

    @Override
    protected Void doInBackground(Void... params) {

        //第一步：创建HttpClient对象
        HttpClient httpClient=new DefaultHttpClient();
        //第二步：创建代表请求的对象,参数是访问的服务器地址
        HttpGet httpGet=new HttpGet(URL);
        //请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);

        //读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

        try {
            //第三步：执行请求，获取服务器发还的相应对象
            HttpResponse httpResponse = httpClient.execute(httpGet);
            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
                //创建一个JSON对象
                JSONObject responseObjec = new JSONObject(response.toString());
                //获取err_code
                int err_code = responseObjec.getInt("err_code");

                if (err_code != 0) {
                    publishProgress("requestError");
                    return null;
                }else {

                    JSONObject data=responseObjec.getJSONObject("data");
                    token=data.getString("token");//换成新token

                    //判断激活码是否过期
                    String newURL="http://api.wxyaoyao.com/3/addon/api?name=client_api&action=get_license_list&token="+token;
                    httpGet=new HttpGet(newURL);
                    httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        entity = httpResponse.getEntity();
                        response = EntityUtils.toString(entity, "utf-8");
                        responseObjec = new JSONObject(response.toString());
                        err_code = responseObjec.getInt("err_code");
                        if (err_code != 0) {
                            publishProgress("requestError");
                            return null;
                        }else {
                            data=responseObjec.getJSONObject("data");
                            JSONArray list=data.getJSONArray("list");
                            if (list != null) {
                                long[] expire_datelineArray = new long[list.length()];
                                boolean allVipIsZero = true;
                                int expireDateIndex = 0;
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject tempObj = list.getJSONObject(i);
                                    JSONObject otherInfo = tempObj.getJSONObject("other_info");
                                    int vip = otherInfo.getInt("vip");
                                    if (vip == 1) {
                                        allVipIsZero = false;
                                        expire_datelineArray[expireDateIndex++] = tempObj.getLong("expire_dateline");
                                    }
                                }
                                if (allVipIsZero) {//如果全部vip都不为1，则未授权
                                    publishProgress("accountNotValid");
                                    return null;
                                } else {
                                    long expire_dateline = 0;
                                    if (expire_datelineArray.length == 1) {
                                        expire_dateline = expire_datelineArray[0];
                                        Date nowDate = new Date();
                                        if (expire_dateline * 1000 < nowDate.getTime()) {//如果有效期比现在早，则告诉用户激活码失效

                                            publishProgress("accountNotValid");
                                            return null;
                                        }

                                    } else {
                                        for (int i = 0; i < expire_datelineArray.length; i++) {
                                            if (expire_datelineArray[i] > expire_dateline)
                                                expire_dateline = expire_datelineArray[i];
                                        }
                                        Date nowDate = new Date();
                                        if (expire_dateline * 1000 < nowDate.getTime()) {//如果有效期比现在早，则告诉用户激活码失效
                                            publishProgress("accountNotValid");
                                            return null;
                                        }
                                    }
                                }
                            }else {//如果list里没有东西，说明没有激活码
                                publishProgress("accountNotValid");
                                return null;
                            }
                        }
                    }else {//状态码不为200
                        publishProgress("statusCodeIsNotValid");
                        return null;
                    }


                    Date date=new Date();

                    SharedPreferences mySharedPreferences= thisFrame.getSharedPreferences("userInfoCache",
                            Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putBoolean("isCached", true);
                    editor.putString("cachedDate",String.valueOf(date.getTime()));
                    editor.putString("chosenOfficialAccountName", chosenOfficialAccount);
                    editor.putString("chosenOfficialAccountHint", chosenOfficialAccountHint);
                    editor.putString("accountName", accountName);
                    editor.putString("chosenOfficialAccountAffliatedCareer", chosenOfficialAccountAffliatedCareer);
                    editor.putString("chosenOfficialAccountHeadImageURl", chosenOfficialAccountHeadImageURl);
                    editor.putString("token", token);
                    editor.commit();


                    Intent intent = new Intent();
                    intent.putExtra("chosenOfficialAccountName", chosenOfficialAccount);
                    intent.putExtra("chosenOfficialAccountHint", chosenOfficialAccountHint);
                    intent.putExtra("accountName", accountName);
                    intent.putExtra("chosenOfficialAccountAffliatedCareer", chosenOfficialAccountAffliatedCareer);
                    intent.putExtra("imageIsURL", true);
                    intent.putExtra("chosenOfficialAccountHeadImageURl", chosenOfficialAccountHeadImageURl);
                    intent.putExtra("token", token);
                    intent.setClass(thisFrame, MainFrame.class);

                    isEnteringMainFrameDialog.dismiss();
                    thisFrame.startActivity(intent);
                    thisFrame.finish();
                }
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
        AlertDialog.Builder builder= new AlertDialog.Builder(thisFrame);

        switch (progress[0]){

            case "requestError":
                builder.setMessage("请求错误");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "statusCodeIsNotValid":
                builder.setMessage("连接异常，请检查网络!");
                builder.setTitle("提示");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case "accountNotValid":
                builder.setMessage("抱歉，未检查到您的账户的授权信息，请确认您是否已激活授权码");
                builder.setTitle("提示");
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
            case "ClientProtocolException":
                builder.setTitle("提示");
                builder.setMessage("出现ClientProtocol异常，请联系开发人员！");
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

        }

    }
}
