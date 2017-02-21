package com.example.xiangjun.qingxinyaoyiyao.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.xiangjun.qingxinyaoyiyao.ui.AddOrEditPageFrame;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;


/**
 * Created by xiangjun on 15/12/25.
 */
public class UploadImageAsyncTask extends AsyncTask<Void, String, Void> {

    private final static int UPLOAD_IMAGE_SUCCESS = 16;
    private final static int UPLOAD_IMAGE_FAILURE = 23;
    private final static int UPLOAD_IMAGE_NOTEXIST = 24;

    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private String URL;//服务器端地址
    private String imgPath;//图片的本地地址

    private Handler listenUploadingImageHandler;

    private AddOrEditPageFrame thisAddOrEditPageFrame;
    public static final String FAILURE = "0";


    public UploadImageAsyncTask(String URL, String imgPath, Handler listenUploadingImageHandler, AddOrEditPageFrame thisAddOrEditPageFrame) {
        this.URL = URL;
        this.imgPath = imgPath;
        this.listenUploadingImageHandler = listenUploadingImageHandler;
        this.thisAddOrEditPageFrame = thisAddOrEditPageFrame;
    }


    /**
     * 上传图片
     *
     * @param url      上传地址
     * @param filepath 图片路径
     * @return
     */
    public void uploadImage(String url, String filepath) {
        UploadUtil uploadUtil = new UploadUtil();
        File file = new File(filepath);
        String result = "";
        if (file.exists()) {
            //获取图片原始大小
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 1;
            opts.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            Bitmap compressedThumbnailBitmap;
            if (width <= 200 && height <= 200)
                result = uploadUtil.uploadFile(file, url);
            else {

                if (width > 200 && height <= 200)
                    compressedThumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, height);
                else if (width <= 200 && height > 200)
                    compressedThumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, 200);
                else
                    compressedThumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, 200);

                final ByteArrayOutputStream baos = new ByteArrayOutputStream();

                StringTokenizer stringTokenizer = new StringTokenizer(
                        imgPath, ".");
                stringTokenizer.nextToken();
                String fileType = stringTokenizer.nextToken();
                if (fileType.equals("png"))
                    compressedThumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                else if (fileType.equals("jpg"))
                    compressedThumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] compressedBytes = baos.toByteArray();

                result = uploadUtil.uploadFile(file.getName(), compressedBytes, url);

            }
        } else{
            result = null;
            Message message = listenUploadingImageHandler.obtainMessage();
            message.what = UPLOAD_IMAGE_NOTEXIST;
            listenUploadingImageHandler.sendMessage(message);
            return;
        }


        if (result.equals(FAILURE)){
            Message message = listenUploadingImageHandler.obtainMessage();
            message.what = UPLOAD_IMAGE_FAILURE;
            listenUploadingImageHandler.sendMessage(message);
        }
        else {
            Message message = listenUploadingImageHandler.obtainMessage();
            message.what = UPLOAD_IMAGE_SUCCESS;
            Bundle bundle = new Bundle();
            bundle.putString("icon_url", result);
            message.setData(bundle);
            listenUploadingImageHandler.sendMessage(message);
        }

    }


    @Override
    protected Void doInBackground(Void... params) {

        uploadImage(this.URL, this.imgPath);


        return null;
    }

    protected void onProgressUpdate(String... progress) {
    }

}
