package com.example.huper.mapalarm.searchView.adapter;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

/**
 * Created by Huper on 2017/5/15.
 */

public class LocationBean {
    public String name;
    public String address;
    public LatLonPoint point;

    public LocationBean(String name, String address, LatLonPoint point){
        this.name = name;
        this.address = address;
        this.point = point;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public LatLonPoint getPoint(){return point;}
}
