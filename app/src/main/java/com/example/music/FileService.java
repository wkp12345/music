package com.example.music;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 下载服务
 * Created by QZD on 2017/9/20.
 */

public class FileService extends IntentService {
    private final String TAG="LOGCAT";
    private int fileLength, downloadLength;//文件大小
    private Handler handler = new Handler();
    public FileService() {
        super("DownLoadService");//这就是个name
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void onHandleIntent(Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String downloadUrl = bundle.getString("download_url");
            File dirs = new File(getFilesDir().getAbsolutePath(),"");//文件保存地址
            if (!dirs.exists()) {// 检查文件夹是否存在，不存在则创建
                dirs.mkdir();
            }

            File file = new File(dirs, bundle.getString("song")+".mp3");//输出文件名
            Log.d(TAG,"下载启动："+downloadUrl+" --to-- "+ file.getPath());
            // 开始下载
            downloadFile(downloadUrl, file);
            // 下载结束
            Log.d(TAG,"下载结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载
     * @param downloadUrl
     * @param file
     */
    public void downloadFile(String downloadUrl, File file){
        FileOutputStream _outputStream;//文件输出流
        try {
            _outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "找不到目录！");
            e.printStackTrace();
            return;
        }
        InputStream _inputStream = null;//文件输入流
        try {
            URL url = new URL(downloadUrl);    //将url转变为URL类对象
            HttpURLConnection _downLoadCon = (HttpURLConnection) url.openConnection();   //打开URL连接，获取URLConnection对象
            _downLoadCon.setRequestMethod("GET");     //设定请求方法为GET
            fileLength = _downLoadCon.getContentLength();  //得到文件大小
            _inputStream = _downLoadCon.getInputStream();   //读取网页内容（字符串流）
            int respondCode = _downLoadCon.getResponseCode();//服务器返回的响应码
            if (respondCode == 200) {
                byte[] buffer = new byte[1024*8];// 数据块，等下把读取到的数据储存在这个数组，这个东西的大小看需要定，不要太小。
                int len;
                while ((len = _inputStream.read(buffer)) != -1) {
                    _outputStream.write(buffer, 0, len);
                    downloadLength = downloadLength + len;
                    //Log.d(TAG, downloadLength + "/" + fileLength );
                }
            } else {
                Log.d(TAG, "respondCode:" + respondCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {//别忘了关闭流
                if (_outputStream != null) {
                    _outputStream.close();
                }
                if (_inputStream != null) {
                    _inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
