package com.example.music;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 创建数据库
 */
public class dbhelper extends SQLiteOpenHelper {
    public static final String CREATE_DIARY = "create table music (" +    //音乐
            "id text primary key ,name text,length text" +
            ")";

    public dbhelper(@Nullable Context context) {
        super(context, "music.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}