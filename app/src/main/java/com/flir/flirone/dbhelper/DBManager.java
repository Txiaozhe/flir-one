package com.flir.flirone.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flir.flirone.imagehelp.MyImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by txiaozhe on 12/02/2017.
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void add(List<MyImage> images) {
        db.beginTransaction();  //开始事务
        try {
            for (MyImage image : images) {
                db.execSQL("INSERT INTO images VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{image.getName(),
                image.getPath(), image.getType(), image.getSize(), image.getTime(),
                image.getMaxTemp(), image.getMaxTempX(), image.getMaxTempY(),
                image.getAverageTemp()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void updateAge(MyImage image) {
//        ContentValues cv = new ContentValues();
//        cv.put("name", image.getName());
//        db.update("images", cv, "name = ?", new String[]{image.getName()});
    }

    public void deleteOldPerson(MyImage image) {
        //db.delete("images", "name >= ?", new String[]{String.valueOf(person.age)});  删除条件
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<MyImage> query() {
        ArrayList<MyImage> images = new ArrayList<MyImage>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            MyImage image = new MyImage();
            image.setName(c.getString(c.getColumnIndex("name")));
            image.setPath(c.getString(c.getColumnIndex("path")));
            image.setType(c.getString(c.getColumnIndex("type")));
            image.setSize(c.getString(c.getColumnIndex("size")));
            image.setTime(c.getString(c.getColumnIndex("time")));
            image.setMaxTemp(c.getString(c.getColumnIndex("maxTemp")));
            image.setMaxTempX(c.getString(c.getColumnIndex("maxTempX")));
            image.setMaxTempY(c.getString(c.getColumnIndex("maxTempY")));
            image.setAverageTemp(c.getString(c.getColumnIndex("averageTemp")));

            images.add(image);
        }
        c.close();
        return images;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM images", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
