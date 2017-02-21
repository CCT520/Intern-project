package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.xiangjun.qingxinyaoyiyao.ui.FrameLibAdapter;
import com.example.xiangjun.qingxinyaoyiyao.ui.MainFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.RefreshableListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PageDeleteRequest {
    private String url = "http://api.wxyaoyao.com/3/addon/api?name=shakearound&action=page_delete";
    private String token;
    private String[] page_ids;
    private String result;
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();

    private MainFrame thisMainFrame;
    private FrameLibAdapter thisFrameLibAdapter;
    private RefreshableListView thisFrameLiblv;
    private int deleteIndex;

    public MainFrame getThisMainFrame() {
        return thisMainFrame;
    }

    public FrameLibAdapter getThisFrameLibAdapter() {
        return thisFrameLibAdapter;
    }

    public RefreshableListView getThisFrameLiblv() {
        return thisFrameLiblv;
    }

    public int getDeleteIndex() {
        return deleteIndex;
    }

    public PageDeleteRequest(String token, RequestData rd,MainFrame thisMainFrame,FrameLibAdapter thisFrameLibAdapter,
                             RefreshableListView thisFrameLiblv,int deleteIndex) {
        this.token = token;
        this.page_ids = rd.getPage_ids();
        this.thisMainFrame=thisMainFrame;
        this.thisFrameLibAdapter=thisFrameLibAdapter;
        this.thisFrameLiblv=thisFrameLiblv;
        this.deleteIndex=deleteIndex;
        this.url = url + "&token=" + this.token;
        params.put("page_id", this.page_ids[0]);

        client.post(url, params, new AsyncHttpResponseHandler() {
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
                    try {
                        JSONObject responseObjec = new JSONObject(result);
                        long err_code=responseObjec.getLong("err_code");
                        if(err_code==0){
                            getThisFrameLibAdapter().deleteItem(getDeleteIndex());
                            getThisFrameLiblv().setAdapter(getThisFrameLibAdapter());

                            AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());  //先得到构造器
                            builder.setTitle("提示"); //设置标题
                            builder.setMessage("删除页面成功"); //设置内容

                            //为了保证按钮的顺序一样，所以只能反过来设置
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            //参数都设置完成了，创建并显示出来
                            builder.create().show();
                        }else if(err_code==9001029){
                            AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());  //先得到构造器
                            builder.setTitle("提示"); //设置标题
                            builder.setMessage("该页面已绑定到设备，请先解绑后再删除"); //设置内容

                            //为了保证按钮的顺序一样，所以只能反过来设置
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            //参数都设置完成了，创建并显示出来
                            builder.create().show();
                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());  //先得到构造器
                            builder.setTitle("提示"); //设置标题
                            builder.setMessage("请求错误！"); //设置内容

                            //为了保证按钮的顺序一样，所以只能反过来设置
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            //参数都设置完成了，创建并显示出来
                            builder.create().show();
                        }


                    } catch (JSONException e) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());  //先得到构造器
                        builder.setTitle("提示"); //设置标题
                        builder.setMessage("JSON包解析出错，请联系开发人员！"); //设置内容

                        //为了保证按钮的顺序一样，所以只能反过来设置
                        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        //参数都设置完成了，创建并显示出来
                        builder.create().show();
                    }

                }else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getThisMainFrame());  //先得到构造器
                    builder.setTitle("提示"); //设置标题
                    builder.setMessage("网络异常，请检查！"); //设置内容

                    //为了保证按钮的顺序一样，所以只能反过来设置
                    builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //参数都设置完成了，创建并显示出来
                    builder.create().show();
                }

            }
        });

    }

    public String getResult() {
        return result;
    }
}
