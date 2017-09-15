package com.example.huper.mapalarm.searchView.db;

/**
 * Created by Huper on 2017/5/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.amap.api.services.core.LatLonPoint;
import com.example.huper.mapalarm.searchView.adapter.LocationBean;

import java.util.ArrayList;

public class SearchHistoryDB extends SQLiteOpenHelper {

    private String name;
    private String address;
    private double pointX;
    private double pointY;
    private LatLonPoint point;
    private LocationBean history;

    public static final String DB_NAME = "SearchHistory_db";

    public static final String TABLE_NAME = "SearchHistory";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + "name text, address text, pointX real, pointY real)";

    public SearchHistoryDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);//创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<LocationBean> queryAllHistory() {
        ArrayList<LocationBean> historys = new ArrayList<>();
        //获取数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //查询表中的数据
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "id desc");
        //获取name列的索引
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            name = cursor.getString(1);
            address = cursor.getString(2);
            pointX = cursor.getDouble(3);
            pointY = cursor.getDouble(4);
            point = new LatLonPoint(pointX,pointY);
            history = new LocationBean(name,address,point);
            historys.add(history);
        }
        cursor.close();//关闭结果集
        db.close();//关闭数据库对象
        return historys;
    }

    public void insertHistory(LocationBean history) {
        SQLiteDatabase db = getWritableDatabase();
        //生成ContentValues对象
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("name", history.name);
        cv.put("address",history.address);
        cv.put("pointX",history.point.getLatitude());
        cv.put("pointY",history.point.getLongitude());
        //调用insert方法，将数据插入数据库
        db.insert(TABLE_NAME, null, cv);
        //关闭数据库
        db.close();
    }

    public void deleteHistory(LocationBean history) {
        SQLiteDatabase db = getWritableDatabase();
        //生成ContentValues对象
        db.delete(TABLE_NAME, "name=?", new String[]{history.name});
        //关闭数据库
        db.close();
    }

    public void deleteAllHistory() {
        SQLiteDatabase db = getWritableDatabase();
        //删除全部数据
        db.execSQL("delete from " + TABLE_NAME);
        //关闭数据库
        db.close();
    }

}