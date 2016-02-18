package com.example.wx091.allfiles.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wx091.allfiles.beans.File_Item;
import com.example.wx091.allfiles.beans.IConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wx091 on 2015/12/13.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
//        db.execSQL("CREATE TABLE IF NOT EXISTS "+IConstant.TABLE_NAME +
//                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, title VARCHAR, describe TEXT)");
    }

    /**
     * add persons
     * @param persons
     */
    public void add(List<File_Item> persons) {
        db.beginTransaction();	//开始事务
        try {
            for (File_Item person : persons) {
                db.execSQL("INSERT INTO "+ IConstant.TABLE_NAME+" VALUES(null, ?, ?, ?)", new Object[]{person.name, person.title, person.describe});
            }
            db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
            db.endTransaction();	//结束事务
        }
    }
    public void add(File_Item person) {
        db.beginTransaction();	//开始事务
        try {
            db.execSQL("INSERT INTO "+ IConstant.TABLE_NAME+" VALUES(?, ?, ?)", new Object[]{person.name, person.title, person.describe});
            db.setTransactionSuccessful();	//设置事务成功完成
        } finally {
            db.endTransaction();	//结束事务
        }
    }

    /**
     * update person's age
     * @param \
     */

    public String getTitle(String name) {
        Cursor c = db.rawQuery("SELECT * FROM "+IConstant.TABLE_NAME+" WHERE name = ?", new String[]{name});
        if(c.moveToNext()){
            return c.getString(c.getColumnIndex("title"));
        }
        return "";
    }

    public void updateTitle(File_Item person) {
        Cursor c = db.rawQuery("SELECT * FROM "+IConstant.TABLE_NAME+" WHERE name = ?", new String[]{person.name});
        if(c.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put("title", person.title);
            db.update(IConstant.TABLE_NAME, cv, "name = ?", new String[]{person.name});
        }else{
            add(person);
        }

    }

    public String getDescribe(String name) {
        Cursor c = db.rawQuery("SELECT * FROM "+IConstant.TABLE_NAME+" WHERE name = ?", new String[]{name});
        if(c.moveToNext()){
            return c.getString(c.getColumnIndex("describe"));
        }
        return "";
    }

    public void updateDescribe(File_Item person) {
        Cursor c = db.rawQuery("SELECT * FROM "+IConstant.TABLE_NAME+" WHERE name = ?", new String[]{person.name});
        if(c.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put("describe", person.describe);
            db.update(IConstant.TABLE_NAME, cv, "name = ?", new String[]{person.name});
        }else{
            add(person);
        }
    }
    /**
     * delete old person
     * @param person
     */
    public void deleteFile_Item(File_Item person) {
        db.delete(IConstant.TABLE_NAME, "name = ?", new String[]{person.name});
    }

    /**
     * query all persons, return list
     * @return List<File_Item>
     */
//    public List<File_Item> query() {
//        ArrayList<File_Item> persons = new ArrayList<File_Item>();
//        Cursor c = queryTheCursor();
//        while (c.moveToNext()) {
//            File_Item person = new File_Item();
//            person._id = c.getInt(c.getColumnIndex("_id"));
//            person.name = c.getString(c.getColumnIndex("name"));
//            person.age = c.getInt(c.getColumnIndex("age"));
//            person.info = c.getString(c.getColumnIndex("info"));
//            persons.add(person);
//        }
//        c.close();
//        return persons;
//    }

    /**
     * query all persons, return cursor
     * @return	Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM "+IConstant.TABLE_NAME, null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
