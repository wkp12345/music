package com.example.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class musicdao {
    private dbhelper dbhelper;// 创建databaseHelper对象
    private SQLiteDatabase db;// 创建SQLiteDatabase对象

    public musicdao(Context context)// 定义构造函数
    {
        dbhelper = new dbhelper(context);// 初始化databaseHelper对象
    }
    public void insert(music music){
        String s="insert into music (id,name,length) values ('"+music.getId()+"','"+
                music.getName()+"','"+music.getLength()+"')";
        db.execSQL(s);
    }
    @SuppressLint("Range")
    public music find(String a) {
        db = dbhelper.getWritableDatabase();
        String sql = "select * from music where id=?";
        String[] selectionArgs = new String[] { a };
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToNext())// 判断Cursor中是否有数据
        {
            music d = new music();
            d.setId(cursor.getString(cursor.getColumnIndex("id")));
            d.setName(cursor.getString(cursor.getColumnIndex("name")));
            d.setLength(cursor.getString(cursor.getColumnIndex("length")));
            return d;
        }
        return null;// 没有返回null
    }
    @SuppressLint("Range")
    public ArrayList<music> findall() {
        ArrayList<music> list = new ArrayList<music>();
        db = dbhelper.getWritableDatabase();
        Cursor cursor = db.query("music",null,null,null,null,null,null);
        // 游标从头读到尾
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            music d = new music();
            d.setId(cursor.getString(cursor.getColumnIndex("id")));
            d.setName(cursor.getString(cursor.getColumnIndex("name")));
            d.setLength(cursor.getString(cursor.getColumnIndex("length")));
            list.add(d);
        }
        return list;
    }
    @SuppressLint("Range")
    public music findname(String a) {
        db = dbhelper.getWritableDatabase();
        String sql = "select * from music where name=?";
        String[] selectionArgs = new String[] { a };
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToNext())// 判断Cursor中是否有数据
        {
            music d = new music();
            d.setId(cursor.getString(cursor.getColumnIndex("id")));
            d.setName(cursor.getString(cursor.getColumnIndex("name")));
            d.setLength(cursor.getString(cursor.getColumnIndex("length")));
            return d;
        }
        return null;// 没有返回null
    }
    public void update(music m){
        this.delete(m);
        this.insert(m);
    }

    public void delete(music d) {
        db = dbhelper.getWritableDatabase();
        String sql = "delete from music where id=?";
        Object bindArgs[] = new Object[] { d.getId() };
        db.execSQL(sql, bindArgs);
    }
    public void close(){
        db = dbhelper.getWritableDatabase();
        if(db != null){
            db.close();
        }
    }

}
