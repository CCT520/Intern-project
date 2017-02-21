package com.example.xiangjun.qingxinyaoyiyao.function;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.xiangjun.qingxinyaoyiyao.ui.DeviceDataFrame;
import com.example.xiangjun.qingxinyaoyiyao.ui.MyChartView;
import com.example.xiangjun.qingxinyaoyiyao.ui.MyChartView.Mstyle;
import com.example.xiangjun.qingxinyaoyiyao.R;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/*
 * String device_id, String major, String minor, 
 *  String uuid,MyChartView mychartview,ListView listview,
 * MainActivity mainactivity
 */
public class GetDeviceDataAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "tian!";
    private String url;
    private String device_id;
    private String major;
    private String uuid;
    private String minor;
    private int begin_date;
    private int end_date;
    int[] ftime = new int[30];
    int[] shake_pv = new int[30];
    HashMap<Integer, Double> map;
    private LineChart mLineChart;
    private List<Map<String, Object>> listitems;
    private SimpleAdapter simpleadapter;
    private ListView listview;
    private DeviceDataFrame thisDeviceDataFrame;
    private int max = 0;  //the maximum value in shake_pv[]

    private AlertDialog loadingDataDialog;


    public GetDeviceDataAsyncTask(String device_id, String major, String minor,
                                  String uuid, LineChart mLineChart, ListView listview, DeviceDataFrame thisDeviceDataFrame, AlertDialog loadingDataDialog) {
        this.device_id = device_id;
        this.major = major;
        this.minor = minor;
        this.uuid = uuid;
        this.loadingDataDialog = loadingDataDialog;
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) - 1970;
        int day = now.get(Calendar.DAY_OF_YEAR) + year * 365 + (year - 1) / 4;
        end_date = day * 24 * 3600 - 86400;    //��ȥ86400����Ϊ���첻����
        begin_date = end_date - 30 * 24 * 3600;
        this.mLineChart = mLineChart;
        this.listview = listview;
        this.thisDeviceDataFrame = thisDeviceDataFrame;
    }

    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute() called");
    }

    //doInBackground�����ڲ�ִ�к�̨����,�����ڴ˷������޸�UI  
    @Override
    protected String doInBackground(String... params) {
        Log.i(TAG, "doInBackground(Params... params) called");
        //��һ��������HttpClient����
        HttpClient httpClient = new DefaultHttpClient();

        List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
        requestParams.add(new BasicNameValuePair("device_id", device_id));
        requestParams.add(new BasicNameValuePair("major", major));
        requestParams.add(new BasicNameValuePair("uuid", uuid));
        requestParams.add(new BasicNameValuePair("minor", minor));
        requestParams.add(new BasicNameValuePair("begin_date", String.valueOf(begin_date)));
        requestParams.add(new BasicNameValuePair("end_date", String.valueOf(end_date)));

        try {
            HttpPost request = new HttpPost(params[0]);
            HttpResponse httpResponse;
            HttpEntity entity = new UrlEncodedFormEntity(requestParams, "UTF-8");
            request.setEntity(entity);
            //请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);

            //读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

            httpResponse = httpClient.execute(request);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                //���岽������Ӧ������ȡ����ݣ��ŵ�entity����
                HttpEntity httpentity = httpResponse.getEntity();

                String response = EntityUtils.toString(httpentity, "utf-8");//��entity���е����ת��Ϊ�ַ�


                try {
                    JSONObject jo = new JSONObject(response.toString());

                    long err_code=jo.getInt("err_code");
                    if(err_code==0){
                        if (jo.getJSONArray("data").length() == 0) {
                            int t = 0;
                            for (int i = begin_date; i <= end_date - 86400; i = i + 86400) {
                                ftime[t] = i;
                                shake_pv[t] = 0;
                                t++;
                            }
                            publishProgress("Update_UI");
                        } else {
                            JSONArray jan = (JSONArray) jo.get("data");
                        /*
                         * this if will be working when jan!=null and
						 * shake_pv[] and ftime[] will be assigned value here
						 * which will be used for drawing chartview
						 */
                            if (jan != null) {
                                int t = 0, p = 0;   //t,p is used as count respectively for ftime[] and shake_pv[]
                                for (int i = begin_date; i <= end_date - 86400; i = i + 86400) {
                                    if (jan.length() != 0) {
                                        JSONObject json = (JSONObject) jan.get(p);
                                        //�����ǻ�ȡ��ҡ�д��������һ�챻ҡ��ʱ�䣩
                                        if (jan.get(p) != null) {
                                            if (i <= json.getInt("ftime") && json.getInt("ftime") <= i + 86400) {
                                                ftime[t] = i + 86400;
                                                shake_pv[t] = json.getInt("shake_pv");
                                                if (max < shake_pv[t])
                                                    max = shake_pv[t];
                                                t++;
                                                p++;
                                            } else {
                                                ftime[t] = i;
                                                shake_pv[t] = 0;
                                                t++;
                                            }
                                        }
                                    } else {
                                        ftime[t] = i;
                                        shake_pv[t] = 0;
                                        t++;
                                    }

                                }
                                System.out.println("��ʼ�����������");
                                publishProgress("Update_UI");
                            }
                        }
                    }else if(err_code==9001001){
                        publishProgress("invalidTimePeriod");
                    }else {
                        publishProgress("invalidParam");
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    /*
                     *  The JSONException will occur when json object out of range,
					 *  so that it will result the remainder objects will not be assigned for value.
					 *  Obviously, only by executing assignment work here can resolve that problem.
					 */
                    int i;
                    for (i = 29; i >= 0; i--) {
                        if (ftime[i] != 0)
                            break;
                    }
                    for (; i < 29; i++) {
                        ftime[i + 1] = ftime[i] + 86400;
                        shake_pv[i + 1] = 0;
                    }
                    publishProgress("Update_UI");
                    e.printStackTrace();
                }

            }
        } catch (ConnectTimeoutException e) {
            publishProgress("requestTimeOut");
        } catch (ClientProtocolException e) {
            publishProgress("ClientProtocolException");
        } catch (IOException e) {
            publishProgress("IOException");
        }
        return "connection failed!!";
    }

    //onProgressUpdate�������ڸ��½����Ϣ  
    @SuppressLint("UseSparseArrays")
    @Override
    protected void onProgressUpdate(String... progresses) {  
        /*
         * *Begin to draw chart and realize listview 
         */
        if (progresses[0].equals("Update_UI")) {
            LineData mLineData = getLineData(ftime, shake_pv);
            showChart(mLineChart, mLineData, Color.rgb(255, 255, 255));
            listitems = new ArrayList<Map<String, Object>>();
            for (int i = 28; i >= 0; i--)     //ʵ����Ӧ��10��ѭ��
            {
                Map<String, Object> listitem = new HashMap<String, Object>();
                Date nowTime = new Date((long) ftime[i] * 1000);
                SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日");
                String retStrFormatNowDate = sdFormatter.format(nowTime);
                listitem.put("Date", retStrFormatNowDate);
                listitem.put("Count", shake_pv[i] + "次");
                listitems.add(listitem);
            }
            simpleadapter = new SimpleAdapter(thisDeviceDataFrame, listitems,
                    R.layout.statistics_item, new String[]{"Date", "Count"},
                    new int[]{R.id.date, R.id.count});

            listview.setAdapter(simpleadapter);

            loadingDataDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisDeviceDataFrame);
            builder.setMessage("加载成功！");
            loadingDataDialog = builder.create();
            loadingDataDialog.show();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            loadingDataDialog.dismiss();

        }else if(progresses[0].equals("requestTimeOut")){
            loadingDataDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisDeviceDataFrame);
            builder.setTitle("提示");
            builder.setMessage("请求超时，请检查网络！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            loadingDataDialog = builder.create();
            loadingDataDialog.show();
        }else if(progresses[0].equals("invalidTimePeriod")){
            loadingDataDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisDeviceDataFrame);
            builder.setTitle("提示");
            builder.setMessage("日期参数错误，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            loadingDataDialog = builder.create();
            loadingDataDialog.show();
        }else if(progresses[0].equals("invalidParam")){
            loadingDataDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisDeviceDataFrame);
            builder.setTitle("提示");
            builder.setMessage("参数错误，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            loadingDataDialog = builder.create();
            loadingDataDialog.show();
        }else if(progresses[0].equals("ClientProtocolException")){
            loadingDataDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisDeviceDataFrame);
            builder.setTitle("提示");
            builder.setMessage("出现ClientProtocol异常，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            loadingDataDialog = builder.create();
            loadingDataDialog.show();
        }else if(progresses[0].equals("IOException")){
            loadingDataDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(thisDeviceDataFrame);
            builder.setTitle("提示");
            builder.setMessage("出现IO异常，请联系开发人员！");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            loadingDataDialog = builder.create();
            loadingDataDialog.show();
        }
    }

    // 设置显示的样式
    private void showChart(LineChart lineChart, LineData lineData, int color) {

        BarLineChartBase.BorderPosition[] mBorderPositions = new BarLineChartBase.BorderPosition[] {
                BarLineChartBase.BorderPosition.LEFT, BarLineChartBase.BorderPosition.BOTTOM,BarLineChartBase.BorderPosition.RIGHT

        };
        lineChart.setBorderPositions(mBorderPositions);//设置左，下，右都有坐标轴

        // no description text
        lineChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("该日期区间无数据，\n请重试");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setBackgroundColor(Color.BLACK & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        lineChart.getXLabels().setSpaceBetweenLabels(15);
        lineChart.getXLabels().setPosition(XLabels.XLabelPosition.BOTTOM);

        lineChart.getYLabels().setPosition(YLabels.YLabelPosition.BOTH_SIDED);


        lineChart.setDrawHorizontalGrid(false);
        lineChart.setDrawVerticalGrid(false);


        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放


        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);//

        lineChart.setBackgroundColor(color);// 设置背景

        // add data
        lineChart.setData(lineData); // 设置数据


        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色
//      mLegend.setTypeface(mTf);// 字体

        lineChart.animateX(2500); // 立即执行的动画,x轴
    }


    private LineData getLineData(int[] ftime, int[] shake_pv) {
        ArrayList<String> xValues = new ArrayList<String>();
        int index_x=0;
        for (int i = 22; i < 29; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            if (i == 22 || i == 25 || i == 28) {
                Date nowTime = new Date((long) ftime[i] * 1000);
                SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy年MM月dd日");
                String retStrFormatNowDate = sdFormatter.format(nowTime);
                xValues.add("" + retStrFormatNowDate);
            } else {
                xValues.add(" ");
            }
        }

        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        int j = 0;
        for (int i = 22; i < 29; i++) {

            float value = shake_pv[i];
            yValues.add(new Entry(value, j));
            j++;

        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "摇一摇次数" /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.15f);//设置曲线平滑度(0.05-1)
        lineDataSet.setLineWidth(3f); // 线宽
        lineDataSet.setColor(Color.rgb(173, 216, 230));// 显示颜色
        lineDataSet.setHighLightColor(Color.rgb(173, 216, 230)); // 高亮的线的颜色

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        return lineData;
    }

} 