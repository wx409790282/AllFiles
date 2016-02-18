package com.example.wx091.allfiles.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wx091.allfiles.beans.IConstant;

/**
 * Created by wx091 on 2015/12/13.
 */
public class DBHelper extends SQLiteOpenHelper {


    //with cloumn name,title,describe, title=name at first time


    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, IConstant.DATABASE_NAME, null, IConstant.DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("drop table "+IConstant.TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+IConstant.TABLE_NAME +
                "(name TEXT PRIMARY KEY, title VARCHAR, describe TEXT)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE "+ IConstant.TABLE_NAME+" ADD COLUMN other STRING");
    }
}

