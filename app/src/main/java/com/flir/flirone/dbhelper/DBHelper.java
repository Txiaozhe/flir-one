package com.flir.flirone.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flir.flirone.GlobalConfig;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, GlobalConfig.DB_NAME, null, GlobalConfig.DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS heatimages" +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + GlobalConfig.TABLE_COLUMN_NAMES);
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
    }
}