package com.example.huper.mapalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

/**
 * Created by dell on 2017/4/18.
 */

public class Database extends SQLiteOpenHelper {

    public SQLiteDatabase dbWrite, dbRead, db;

    public Database(Context context) {
        super(context, "db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table EventData(" +
                "_id integer primary key autoincrement," +
                "EventName char(30)," +
                "EventLocationDes char(50)," +
                "latitude double," +
                "longitude double," +
                "alarmDis integer check(alarmDis>=0)," +
                "alarmedThisTime boolean,"+
                "isActive boolean)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor queryDatabase(){
        dbRead = this.getReadableDatabase();
        return dbRead.query("EventData", new String[]{"EventName", "EventLocationDes", "latitude", "longitude", "alarmDis", "alarmedThisTime","isActive"}, null, null, null, null, null);
    }

    public int getTotalEventNum(){
        int cnt=0;
        Cursor c=queryDatabase();
        while(c.moveToNext()){
            cnt++;
        }
        return cnt;
    }

    public boolean deleteFromDatabase(int position, final List<AlarmData> alarmDatas){
        int deleteID=0;
        dbRead = MainActivity.db.getReadableDatabase();
        Cursor c = dbRead.query("EventData", new String[]{"EventName", "EventLocationDes","_id"}, null, null, null, null, null);
        while(c.moveToNext()){
            if (String.valueOf(alarmDatas.get(position).todo).equals(c.getString(c.getColumnIndex("EventName")))&&
                    String.valueOf(alarmDatas.get(position).address).equals(c.getString(c.getColumnIndex("EventLocationDes")))){
                deleteID=c.getInt(c.getColumnIndex("_id"));
                dbWrite=MainActivity.db.getWritableDatabase();
                dbWrite.delete("EventData","_id="+deleteID,null);
                dbWrite.close();
                return true;
            }
        }
        c.close();
        dbRead.close();
        return false;
    }

    public long insertData(Bundle bundle){
        ContentValues cv=new ContentValues();
        if (findID(bundle.getString("Event_name"),bundle.getString("Location_des"))!=-1) {
            return -1;
        }
        dbWrite=MainActivity.db.getWritableDatabase();
        cv.put("EventName",bundle.getString("Event_name"));
        cv.put("EventLocationDes",bundle.getString("Location_des"));
        cv.put("latitude",bundle.getDouble("latitude"));
        cv.put("longitude",bundle.getDouble("longitude"));
        cv.put("alarmDis",bundle.getInt("Alarm_dis"));
        cv.put("alarmedThisTime",false);
        cv.put("isActive",true);
        long res = dbWrite.insert("EventData",null,cv);
        dbWrite.close();
        return res;
    }

    public boolean updateActive(Database db, AlarmData item, boolean updateto){
        int updateID = -1;
        dbRead = MainActivity.db.getReadableDatabase();
        Cursor c = dbRead.query("EventData", new String[]{"EventName", "EventLocationDes", "_id"}, null, null, null, null, null);
        while (c.moveToNext()) {
            if (String.valueOf(item.todo).equals(c.getString(c.getColumnIndex("EventName"))) &&
                    String.valueOf(item.address).equals(c.getString(c.getColumnIndex("EventLocationDes")))) {
                updateID = c.getInt(c.getColumnIndex("_id"));
                break;
            }
        }
        if (updateID==-1){
            return false;
        }
        c.close();
        dbRead.close();
        dbWrite = MainActivity.db.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("isActive",updateto);
        if (!updateto){
            item.nowdis=-1;
            item.alarmedThisTime=false;
            cv.put("alarmedThisTime", false);
        }
        dbWrite.update("EventData",cv,"_id="+updateID,null);
        dbWrite.close();
        return true;
    }

    public void setOffAlarm(int _id){
        dbWrite=MainActivity.db.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("isActive", false);
        dbWrite.update("EventData",cv,"_id="+_id,null);
        dbWrite.close();
    }

    public void updateThisTime(int _id, boolean updateto){
        dbWrite=MainActivity.db.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("alarmedThisTime",updateto);
        dbWrite.update("EventData",cv,"_id="+_id,null);
        dbWrite.close();
    }

    public boolean updateAllInfo(int _id,Bundle bundle,int clickPosition){
        ContentValues cv=new ContentValues();
        if(findID(bundle.getString("Event_name"),bundle.getString("Location_des"))!=-1&&
                findID(bundle.getString("Event_name"),bundle.getString("Location_des"))!=_id){
            return false;
        }
        cv.put("EventName",bundle.getString("Event_name"));
        cv.put("EventLocationDes",bundle.getString("Location_des"));
        cv.put("latitude",bundle.getDouble("latitude"));
        cv.put("longitude",bundle.getDouble("longitude"));
        cv.put("alarmDis",bundle.getInt("Alarm_dis"));
        cv.put("alarmedThisTime",false);
        cv.put("isActive",true);
        dbWrite=MainActivity.db.getWritableDatabase();
        dbWrite.update("EventData",cv,"_id="+_id,null);
        dbWrite.close();
        return true;
    }

    public int findID(String targetName, String targetLoc){
        dbRead = MainActivity.db.getReadableDatabase();
        Cursor c = dbRead.query("EventData", new String[]{"EventName", "EventLocationDes","_id"}, null, null, null, null, null);
        while(c.moveToNext()){
            if (c.getString(c.getColumnIndex("EventName")).equals(targetName)&&
                    c.getString(c.getColumnIndex("EventLocationDes")).equals(targetLoc)){
                return c.getInt(c.getColumnIndex("_id"));
            }
        }
        c.close();
        dbRead.close();
        return -1;
    }

    public int findID(int clickPosition){
        dbRead=MainActivity.db.getReadableDatabase();
        Cursor c = dbRead.query("EventData", new String[]{"EventName", "EventLocationDes","_id"}, null, null, null, null, null);
        while(c.moveToNext()) {
            if (MainActivity.alarmDatas.get(clickPosition).address.equals(c.getString(c.getColumnIndex("EventLocationDes")))
                    &&MainActivity.alarmDatas.get(clickPosition).todo.equals(c.getString(c.getColumnIndex("EventName")))){
                return c.getInt(c.getColumnIndex("_id"));
            }
        }
        c.close();
        dbRead.close();
        return -1;
    }
}
