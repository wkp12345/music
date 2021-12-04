package com.example.music;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HttpClient {
    private static Gson jsonConvert = new Gson();

    private static OkHttpClient okHttpClient = new OkHttpClient();  //创建OkHttpClient对象
    private static final String SERVER_HOST = "\n" +
            "https://cloud-music-api-f494k233x-mgod-monkey.vercel.app/search?keywords= ";  //音乐查询API服务地址
    private static final String SERVER_HOST1 = "\n" +
            "https://cloud-music-api-f494k233x-mgod-monkey.vercel.app/song/url?id= ";  //音乐url查询API服务地址
    public static <T> void query(String name,final Class<T> tClass
            , final IHttpCallback callback){
        String parameters = name;  //请求参数

        Request request = new Request.Builder()
                .url(SERVER_HOST + parameters) //请求接口
                .get()
                .build();   //创建Request 对象
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onSuccess(null,false);
            }
            @Override
            public void onResponse(Response response) throws IOException {   //得到Response 对象
                try{
                    callback.onSuccess(jsonConvert.fromJson(response.body().string(),tClass),  //fromJson方法可以将Json字符串转换成JavaBean，
                            true);                                                    // 但是要解析的JavaBean的属性必须
                                                                                              //是Json字符串中的字段，可以少于Json字符串中的字段。
                }catch (Exception e){
                    System.out.println("--------------0"+"输入错误");
                    e.printStackTrace();
                }
            }
        });
    }

    public static <T> void geturl(String id,final Class<T> tClass
            , final IHttpCallback callback){
        String parameters = id;  //请求参数

        Request request = new Request.Builder()
                .url(SERVER_HOST1 + parameters) //请求接口
                .get()
                .build();   //创建Request 对象

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onSuccess(null,false);
            }

            @Override
            public void onResponse(Response response) throws IOException {   //得到Response 对象
                try{
                    callback.onSuccess(jsonConvert.fromJson(response.body().string(),tClass),  //fromJson方法可以将Json字符串转换成JavaBean，
                            true);                                                    // 但是要解析的JavaBean的属性必须
                    //是Json字符串中的字段，可以少于Json字符串中的字段。
                }catch (Exception e){
                    System.out.println("--------------1"+"输入错误");
                    e.printStackTrace();
                }
            }
        });
    }

    public interface IHttpCallback {
        <T> void onSuccess(T result,boolean isSuccess);
    }

}
