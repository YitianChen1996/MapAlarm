package com.example.huper.mapalarm;

/**
 * Created by Huper on 2017/4/7.
 */

public class AlarmData {
    String address;
    String frequency;
    String todo;
    boolean state;
    double latitude;
    double longitude;
    int alarmDis;
    double nowdis=-1;
    boolean alarmedThisTime=false;
    /*有可能改变alarmedThisTime的地方：
    1.MyDialogActivity*/

    public AlarmData(String  address, String todo, boolean state,double latitude, double longitude, int alarmDis){
        this.address = address;
        this.todo = todo;
        this.state = state;
        this.latitude=latitude;
        this.longitude=longitude;
        this.alarmDis=alarmDis;
    }
}
