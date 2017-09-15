package com.example.huper.mapalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

/**
 * Created by dell on 2017/4/18.
 */

/*可能增加活跃事件的地方：
* 1.主界面的点击（MyAdapter中）
* 2.创建新事件或修改事件（MainActivity onActivityResult中）
* */
/*可能减少活跃事件的地方：
* 1.主界面点击（MyAdapter中）
* 2.删除事件（MainActivity onClick中）*/


public class BackgroundLocation {
    public static int activeEventNum=0;
    Context context;
    public AMapLocationClient mLocationClient;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationOption;
    String currentCityName, currentCityCode;

    public BackgroundLocation(Context context) {
        this.context=context;

    }

    public static int getActiveEventNum(){
        activeEventNum=0;
        for (int i=0;i<=MainActivity.alarmDatas.size()-1;i++){
            if (MainActivity.alarmDatas.get(i).state){
                activeEventNum++;
            }
        }
        return activeEventNum;
    }

    public double poll(LatLng myLocation){//轮询所有活跃事件，返回最小活跃事件距离
        double mindis=-1;

        for (int i=0;i<=MainActivity.alarmDatas.size()-1;i++){//轮询所有事件
            if (MainActivity.alarmDatas.get(i).state) {//如果此事件当前是活跃状态
                LatLng tarlatLng=new LatLng(MainActivity.alarmDatas.get(i).latitude,MainActivity.alarmDatas.get(i).longitude);//获得此事件坐标
                double thisdis=AMapUtils.calculateLineDistance(myLocation,tarlatLng);
                MainActivity.alarmDatas.get(i).nowdis=thisdis;
                MainActivity.myAdapter.notifyItemChanged(i);//刷新界面
                if (mindis==-1||thisdis<mindis){
                    mindis=thisdis;
                }
                if(thisdis<=MainActivity.alarmDatas.get(i).alarmDis&&!MainActivity.alarmDatas.get(i).alarmedThisTime){
                    String temp1 = MainActivity.alarmDatas.get(i).address;
                    String temp2 = MainActivity.alarmDatas.get(i).todo;
                    MyNotification myNotification = new MyNotification(context,temp1,temp2);
                    myNotification.display();
                    Intent intent = new Intent(context, MyDialogActivity.class);
                    Bundle sendbundle=new Bundle();
                    sendbundle.putString("location",temp1);
                    sendbundle.putString("eventname",temp2);
                    sendbundle.putInt("position",i);
                    sendbundle.putInt("databaseID",MainActivity.db.findID(temp2,temp1));
                    intent.putExtras(sendbundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    //Toast.makeText(context,"已经到达"+MainActivity.alarmDatas.get(i).address,Toast.LENGTH_SHORT).show();
                }
                if (MainActivity.alarmDatas.get(i).alarmedThisTime&&thisdis>MainActivity.alarmDatas.get(i).alarmDis){
                    MainActivity.db.updateThisTime(MainActivity.db.findID(i),false);
                    MainActivity.alarmDatas.get(i).alarmedThisTime=false;
                }
            }
        }
        return mindis;
    }

    public void startLocation(){
        mLocationClient = null;
        mLocationListener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation.getErrorCode()!=0){
                    Toast.makeText(context,MyLatLng.showErrInfo(aMapLocation.getErrorCode()),Toast.LENGTH_SHORT).show();
                }
                //System.out.println("定位成功>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                //Toast.makeText(context,showLocationSource(aMapLocation),Toast.LENGTH_SHORT).show();
                double mindis=poll(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()));
                if (mindis>=1000||aMapLocation.getSpeed()<=0.3){//如果最小事件距离大于一公里或者移动速度小于0.3米每秒(龟速，可以判定为静止)
                    mLocationOption.setInterval(10000);
                    mLocationClient.setLocationOption(mLocationOption);
                }
                else{
                    mLocationOption.setInterval(5000);
                    mLocationClient.setLocationOption(mLocationOption);
                }
            }
        };
        initial();
        mLocationClient.startLocation();
    }

    public String showLocationSource(AMapLocation aMapLocation) {
        switch (aMapLocation.getLocationType()) {
            case (AMapLocation.LOCATION_TYPE_WIFI):
                return ("WIFI！！！！！！！！！！！！！！！！！！！！！！！！！！！");
            case (AMapLocation.LOCATION_TYPE_GPS):
                return ("GPS！！！！！！！！！！！！！！！！！！！！！！！！！！！");
            case (AMapLocation.LOCATION_TYPE_CELL):
                return ("Cell!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            case (AMapLocation.LOCATION_TYPE_SAME_REQ):
                return ("位移传感器！！！！！！！！！！！！！！！！！！！！！！！！！！");
        }
        return "未知来源";
    }

    private void initial(){//设置首次定位参数
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(3000);
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void stopLocation(){
        mLocationClient.stopLocation();
    }

}
