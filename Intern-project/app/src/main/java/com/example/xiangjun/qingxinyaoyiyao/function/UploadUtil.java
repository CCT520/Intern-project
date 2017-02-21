package com.example.xiangjun.qingxinyaoyiyao.function;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 上传工具类
 *
 * @author ICQwlj<br>
 *         Email :wlj250237@126.com<br>
 *         支持上传文件和参数
 */
public class UploadUtil {
    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

    private static final int TIME_OUT = 10 * 10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";


    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file, String RequestURL) {
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);
            //设置编码
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setRequestProperty("Accept", "*/*");
            if (file != null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam = conn.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();

                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"content_upload_type\"" + LINE_END);
                sb.append("1" + LINE_END);

                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"content\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: image/png" + LINE_END);

                String sbToString = sb.toString();

                dos.writeUTF(sbToString);

                dos.writeUTF(LINE_END);

                //以下是传图片的二进制数组
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {

                    dos.write(bytes, 0, len);
                }

                //以下是结尾符
                StringBuffer endSb = new StringBuffer();
                endSb.append(LINE_END);
                endSb.append(PREFIX);
                endSb.append(BOUNDARY);
                endSb.append("--");//加上结尾符
                endSb.append(LINE_END);

                dos.writeUTF(endSb.toString());
                dos.flush();
                is.close();
                dos.close();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    /* 取得Response内容 */
                    InputStream is2 = conn.getInputStream();
                    int ch;
                    StringBuffer b = new StringBuffer();
                    while ((ch = is2.read()) != -1) {
                        b.append((char) ch);
                    }

                    String result = new String(b.toString().getBytes(), "utf-8");
                    //创建一个JSON对象
                    JSONObject responseObjec = new JSONObject(result.toString());

                    String err_msg = responseObjec.getString("err_msg");

                    if (err_msg.equals("success")) {
                        JSONObject data = responseObjec.getJSONObject("data");
                        String resource_url = data.getString("resource_url");

                        return resource_url;
                    } else {

                        return FAILURE;
                    }


                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

    public static String uploadFile(String fileName,byte[] uploadFileBytes, String RequestURL) {
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);
            //设置编码
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setRequestProperty("Accept", "*/*");
            if (uploadFileBytes != null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam = conn.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();

                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"content_upload_type\"" + LINE_END);
                sb.append("1" + LINE_END);

                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"content\"; filename=\"" + fileName + "\"" + LINE_END);
                sb.append("Content-Type: image/png" + LINE_END);

                String sbToString = sb.toString();

                dos.writeUTF(sbToString);

                dos.writeUTF(LINE_END);

                //以下是传图片的二进制数组
                dos.write(uploadFileBytes);

                //以下是结尾符
                StringBuffer endSb = new StringBuffer();
                endSb.append(LINE_END);
                endSb.append(PREFIX);
                endSb.append(BOUNDARY);
                endSb.append("--");//加上结尾符
                endSb.append(LINE_END);

                dos.writeUTF(endSb.toString());
                dos.flush();
                dos.close();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    /* 取得Response内容 */
                    InputStream is2 = conn.getInputStream();
                    int ch;
                    StringBuffer b = new StringBuffer();
                    while ((ch = is2.read()) != -1) {
                        b.append((char) ch);
                    }

                    String result = new String(b.toString().getBytes(), "utf-8");
                    //创建一个JSON对象
                    JSONObject responseObjec = new JSONObject(result.toString());

                    String err_msg = responseObjec.getString("err_msg");

                    if (err_msg.equals("success")) {
                        JSONObject data = responseObjec.getJSONObject("data");
                        String resource_url = data.getString("resource_url");

                        return resource_url;
                    } else {

                        return FAILURE;
                    }


                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

}
