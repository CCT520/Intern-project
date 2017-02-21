package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.MyInfoAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * Created by xiangjun on 15/12/23.
 */
public class UserProfileAsyncTask extends AsyncTask<Void, String, Void> {


    private String URL;
    private String token;
    private MyInfoAdapter myInfoAdapter;
    private ListView myInfoLv;
    private MainFrame thisMainFrame;

    public MainFrame getThisMainFrame() {
        return thisMainFrame;
    }

    public UserProfileAsyncTask(String URL,String token,MyInfoAdapter myInfoAdapter, ListView myInfoLv, MainFrame thisMainFrame) {
        this.URL = URL;
        this.token=token;
        this.myInfoAdapter = myInfoAdapter;
        this.myInfoLv = myInfoLv;
        this.thisMainFrame = thisMainFrame;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //第一步：创建HttpClient对象
        HttpClient httpClient = new DefaultHttpClient();
        //第二步：创建代表请求的对象,参数是访问的服务器地址
        HttpGet httpGet = new HttpGet(URL);
        //第三步：执行请求，获取服务器发还的相应对象
        HttpResponse httpResponse = null;
        //请求超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);

        //读取超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
        try {
            httpResponse = httpClient.execute(httpGet);
            //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //第五步：从相应对象当中取出数据，放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
                //创建一个JSON对象
                JSONObject responseObjec = new JSONObject(response.toString());

                int err_code = responseObjec.getInt("err_code");
                if (err_code == 0) {
                    JSONObject data = responseObjec.getJSONObject("data");

                    //获取账户余额
                    double money_balance = data.getDouble("money_balance");
                    DecimalFormat myformat = new DecimalFormat("###,##0.00");
                    String formattedMoneyBalance = myformat.format(money_balance);
                    myInfoAdapter.setItem(6, "myFinanceRemain", formattedMoneyBalance + "元");

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
                        } else {
                            data = responseObjec.getJSONObject("data");
                            //获取有效期
                            JSONArray list = data.getJSONArray("list");
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
                                if (allVipIsZero) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(thisMainFrame);
                                    builder.setMessage("抱歉，未检查到您的账户的授权信息，请确认您是否已激活授权码");
                                    builder.setTitle("提示");
                                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                    return null;
                                } else {
                                    long expire_dateline = 0;
                                    if (expire_datelineArray.length == 1) {
                                        expire_dateline = expire_datelineArray[0];
                                        Date nowDate = new Date();
                                        if (expire_dateline * 1000 < nowDate.getTime()) {//如果有效期比现在早，则告诉用户激活码失效
                                            AlertDialog.Builder builder = new AlertDialog.Builder(thisMainFrame);
                                            builder.setMessage("抱歉，未检查到您的账户的授权信息，请确认您是否已激活授权码");
                                            builder.setTitle("提示");
                                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                            return null;
                                        } else {
                                            Date date = new Date(expire_dateline * 1000);
                                            DateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日");
                                            String formattedDate = df1.format(date);
                                            myInfoAdapter.setItem(5, "ValidDate", formattedDate);
                                        }

                                    } else {
                                        for (int i = 0; i < expire_datelineArray.length; i++) {
                                            if (expire_datelineArray[i] > expire_dateline)
                                                expire_dateline = expire_datelineArray[i];
                                        }
                                        Date nowDate = new Date();
                                        if (expire_dateline * 1000 < nowDate.getTime()) {//如果有效期比现在早，则告诉用户激活码失效
                                            AlertDialog.Builder builder = new AlertDialog.Builder(thisMainFrame);
                                            builder.setMessage("抱歉，未检查到您的账户的授权信息，请确认您是否已激活授权码");
                                            builder.setTitle("提示");
                                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.create().show();
                                            return null;
                                        } else {
                                            Date date = new Date(expire_dateline * 1000);
                                            DateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日");
                                            String formattedDate = df1.format(date);
                                            myInfoAdapter.setItem(5, "ValidDate", formattedDate);
                                        }
                                    }
                                }
                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(thisMainFrame);
                                builder.setMessage("抱歉，未检查到您的账户的授权信息，请确认您是否已激活授权码");
                                builder.setTitle("提示");
                                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                return null;
                            }

                            myInfoLv.setAdapter(myInfoAdapter);
                        }
                    }



                } else {
                    String err_msg = responseObjec.getString("err_msg");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                    builder.setTitle("提示");
                    builder.setMessage("在加载用户信息时发生了未知错误\n" +
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


            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
                builder.setTitle("提示");
                builder.setMessage("加载用户信息时网络异常，请检查！");
                builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        } catch (ConnectTimeoutException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
            builder.setTitle("提示");
            builder.setMessage("加载用户信息时请求超时，请检查网络！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
            builder.setTitle("提示");
            builder.setMessage("加载用户信息时出现IO异常，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } catch (JSONException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getThisMainFrame());
            builder.setTitle("提示");
            builder.setMessage("加载用户信息时JSON包解析出错，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }


        return null;
    }
}
