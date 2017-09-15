package com.example.huper.mapalarm;

import com.amap.api.maps.model.LatLng;

/**
 * Created by dell on 2017/4/18.
 */

public class MyLatLng {
    public static String showLatLngInfo(LatLng latLng){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.000");
        String latitude_info,longitude_info;
        if (latLng.latitude>=0){
            latitude_info=df.format(latLng.latitude)+"°N";
        }else{
            latitude_info=df.format(Math.abs(latLng.latitude))+"°S";
        }
        if (latLng.longitude>=0){
            longitude_info=df.format(latLng.longitude)+"°E";
        }else {
            longitude_info=df.format(Math.abs(latLng.longitude))+"°W";
        }
        return "纬度："+latitude_info+"   经度："+longitude_info;
    }
    public static String showErrInfo(int errcode){
        switch (errcode){
            case(4):{return "您的网络似乎有问题 (⊙ˍ⊙)";}
            case(12):{return "缺少定位权限";}
            default:{return "错误代码"+errcode;}
        }
    }
}
