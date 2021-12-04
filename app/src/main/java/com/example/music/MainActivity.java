package com.example.music;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText musicid;  //根据音乐名查询音乐
    private Button query;     //查询
    private ListView listview;
    private musicdao dao;   //对音乐数据库进行增删改查
    private ArrayList<music> list;   //音乐列表
    private ArrayList<String> namelist;  //已下载的音乐列表
    private MyBaseAdapter myadapter;
    private String url;  //音乐的url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicid=findViewById(R.id.musicid);
        query=findViewById(R.id.query);
        listview=findViewById(R.id.listview);
        dao=new musicdao(MainActivity.this);
        list=dao.findall();
        initnamelist();
        myadapter=new MyBaseAdapter();
        listview.setAdapter(myadapter);

        /*点击下载如果已下载则播放音乐*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                url=list.get(position).getUrl();
                String ss=getFilesDir().getAbsolutePath()+"/"+list.get(position).getName()+".mp3";   //查看该音乐文件是否存在
                if(hasdownload(ss)){   //存在则播放
                    initnamelist();
                    //创建Intent对象，参数就是从frag1跳转到MusicActivity
                    Intent intent=new Intent(MainActivity.this, Music_Activity.class);
                    intent.putExtra("name",list.get(position).getName());  //把被点击的音乐名字
                    intent.putExtra("list",namelist);                      //和所有已下载的音乐名字传过去
                    //开始跳转
                    startActivity(intent);
                }else{               //不存在就下载
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    //设置弹出框标题
                    builder.setTitle("是否下载歌曲？");
                    builder.setItems(new String[]{"是","否"}, new DialogInterface.OnClickListener() {
                        //类型码
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent1=new Intent(MainActivity.this,FileService.class);
                                    intent1.putExtra("download_url",url);
                                    intent1.putExtra("song",list.get(position).getName());
                                    startService(intent1);
                                    break;
                                case 1:
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        });

        /* 长按删除该音乐。*/
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //设置弹出框标题
                builder.setTitle("确定删除歌曲吗？");
                builder.setItems(new String[]{"删除","取消"}, new DialogInterface.OnClickListener() {
                    //类型码
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //删除对应的item索引
                                deleteFile(getFilesDir().getAbsolutePath()+"/"+list.get(position).getName()+".mp3");
                                dao.delete(list.get(position));
                                namelist.remove(list.get(position));
                                list.remove(position);
                                //刷新适配器
                                myadapter.notifyDataSetChanged();//涉及到观察者模式
                                break;
                            case 1:
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    /**
     * 初始化已下载的音乐
     */
    private void initnamelist(){
        namelist=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            String ss=getFilesDir().getAbsolutePath()+"/"+list.get(i).getName()+".mp3";
            if(hasdownload(ss)){
                String s=list.get(i).getName();
                namelist.add(s);
            }
        }
    }

    /**
     * 根据音乐名查询音乐
     * @param v
     */
    public void query(View v){
        String s=musicid.getText().toString();
        HttpClient.query(s,result.class, new HttpClient.IHttpCallback() {
            @Override
            public <T> void onSuccess(T result, boolean isSuccess) {
                if (isSuccess) {
                    result r = (result) result;
                    if (r.getResult().getSongs()!=null && !r.getResult().getSongs().isEmpty()) {
                        music live = r.getResult().getSongs().get(0);
                        System.out.println("*****************"+live.getId());
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(!exist(live)){
                                    geturl(live);             //得到音乐的URL并把音乐存进数据库
                                }else{
                                    System.out.println("+********该歌曲已存在");
                                }
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "--error--", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "无法提供音乐信息", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * 根据音乐id得到音乐的url并把音乐存进数据库
     * @param mm
     */
    private void geturl(music mm){
        HttpClient.geturl(mm.getId(),musicurl.class, new HttpClient.IHttpCallback() {
            @Override
            public <T> void onSuccess(T result, boolean isSuccess) {
                if (isSuccess) {
                    musicurl m = (musicurl) result;
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            if (m.getData()!=null) {
                                url=m.getData().get(0).getUrl();
                                mm.setUrl(url);
                                dao.insert(mm);
                                list.add(mm);
                                System.out.println("*****************"+m.getData().get(0).getUrl());
                                myadapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 判断音乐是否存在数据库中
     * @param m
     * @return
     */
    public boolean exist(music m){
        if(dao.find(m.getId())!=null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断音乐是否下载
     * @param strFile
     * @return
     */
    public boolean hasdownload(String strFile){
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * 根据文件路径删除文件
     * @param fileName
     * @return
     */
    public boolean deleteFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.isFile() && file.exists()) {
                file.delete();
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        }catch (Exception e){
            System.out.println("error");
        }
        return false;
    }

    /**
     * 适配器
     */
    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {//组装数据
            View view=View.inflate(MainActivity.this,R.layout.music_item,null);//在list_item中有两个id,现在要把他们拿过来
            TextView mTextView=(TextView) view.findViewById(R.id.item);
            //组件一拿到，开始组装
            mTextView.setText(list.get(position).getName());  //逆序显示缓存的查询结果，最近一次的查询显示在最上面
            //组装玩开始返回
            return view;
        }
    }

}