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

/**
 * 数据库名：images_info.db
 * 表名：images
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

    public int add(MyImage image) {
        int stateCode = 0;
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO heatimages VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{image.getIsUpLoad(),
                    image.getTeleimei(), image.getBarcode(), image.getPath(), image.getImagename(),
                    image.getImagetime(), image.getMaxtemperature(), image.getMaxtemplocalx(),
                    image.getMaxtemplocaly(), image.getMeantemperature()});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();//结束事务
            stateCode = -1;
        }
        return stateCode;
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
     *
     * @return List<Person>
     */
    public List<MyImage> query() {
        ArrayList<MyImage> images = new ArrayList<MyImage>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            MyImage image = new MyImage();
            image.setIsUpLoad(c.getString(c.getColumnIndex("isUpload")));
            image.setTeleimei(c.getString(c.getColumnIndex("teleimei")));
            image.setBarcode(c.getString(c.getColumnIndex("barcode")));
            image.setPath(c.getString(c.getColumnIndex("path")));
            image.setImagename(c.getString(c.getColumnIndex("imagename")));
            image.setImagetime(c.getString(c.getColumnIndex("imagetime")));
            image.setMaxtemperature(c.getString(c.getColumnIndex("maxtemperature")));
            image.setMaxtemplocalx(c.getString(c.getColumnIndex("maxtemplocalx")));
            image.setMaxtemplocaly(c.getString(c.getColumnIndex("maxtemplocaly")));
            image.setMeantemperature(c.getString(c.getColumnIndex("meantemperature")));

            images.add(image);
        }
        c.close();
        closeDB();
        return images;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM heatimages", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
