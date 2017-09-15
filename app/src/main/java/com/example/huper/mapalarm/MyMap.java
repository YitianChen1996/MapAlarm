package com.example.huper.mapalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.LocationListener;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.List;
import java.util.Timer;

/**
 * Created by dell on 2017/4/14.
 */

public class MyMap {
    Context context;
    AMap amap;
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    MyLocationStyle myLocationStyle;
    double afterLongClick_latitude = -1, afterLongClick_longitude = -1;
    String afterLongClick_description, afterLongClick_eventname;
    Marker mylocation;
    boolean firstTime = true;
    LatLng afterDraglatlng;
    Circle tempCircle;
    String currentCityName;

    public MyMap(AMap amap, Context context) {
        this.context = context;
        this.amap = amap;
        amap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
    }

    public void startLocation(final boolean onlyOnce) {//onlyOnce只获取到currentcityname后就退出
        amap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
                if (mLocationClient == null) {
                    mLocationClient = new AMapLocationClient(context);
                    mLocationOption = new AMapLocationClientOption();
                    mLocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation aMapLocation) {
                            if (mListener != null && aMapLocation != null) {
                                if (aMapLocation.getErrorCode() == 0) {//定位成功
                                    //Toast.makeText(context, "定位成功", Toast.LENGTH_SHORT).show();
                                    currentCityName = aMapLocation.getCity();
                                    if (onlyOnce){
                                        mLocationClient.stopLocation();
                                        return;
                                    }
                                    if (firstTime == true) {
                                        movetoLatlng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), false);
                                        mListener.onLocationChanged(aMapLocation);
                                        zoomin(4);
                                        firstTime = false;
                                    }
                                    if (mylocation != null) {
                                        mylocation.setPosition(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                                        mylocation.showInfoWindow();
                                    } else {
                                        BitmapDescriptor myicon;
                                        mylocation = amap.addMarker(new MarkerOptions().title("您现在所处位置").position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point))
                                                .snippet("长按地图任意点新建提醒"));
                                        startGrowAnimation(mylocation);
                                        mylocation.showInfoWindow();
                                    }
                                } else {//定位失败
                                    Toast.makeText(context, MyLatLng.showErrInfo(aMapLocation.getErrorCode()), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    mLocationOption.setInterval(2000);
                    //mLocationOption.setOnceLocation(true);
                    mLocationOption.setLocationCacheEnable(true);
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    mLocationClient.setLocationOption(mLocationOption);
                    mLocationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {
                mListener = null;
                if (mLocationClient != null) {
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                }
            }
        });
        amap.setMyLocationEnabled(true);
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        BitmapDescriptor myicon;
        myicon = generateBitmap("transparent.png", 100, 200);
        myLocationStyle.myLocationIcon(myicon);
        myLocationStyle.strokeColor(0);
        myLocationStyle.radiusFillColor(0);
        amap.setMyLocationStyle(myLocationStyle);
    }

    public void showLocationSource(AMapLocation aMapLocation) {
        switch (aMapLocation.getLocationType()) {
            case (AMapLocation.LOCATION_TYPE_WIFI):
                System.out.println("WIFI！！！！！！！！！！！！！！！！！！！！！！！！！！！");
                break;
            case (AMapLocation.LOCATION_TYPE_GPS):
                System.out.println("GPS！！！！！！！！！！！！！！！！！！！！！！！！！！！");
                break;
            case (AMapLocation.LOCATION_TYPE_CELL):
                System.out.println("Cell!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                break;
            case (AMapLocation.LOCATION_TYPE_SAME_REQ):
                System.out.println("位移传感器！！！！！！！！！！！！！！！！！！！！！！！！！！");
                break;
        }
    }

    public void setOnLongClickListener() {
        amap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                amap.clear();

                final GeocodeSearch geocodeSearch = new GeocodeSearch(context);
                geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                        if (i == 0) {
                            System.out.println("i=0,failed!!!!!!!!!!!!!!!!!!!!!!!!!");
                        } else {
                            List<PoiItem> poiItemList;
                            poiItemList = regeocodeResult.getRegeocodeAddress().getPois();
                            afterLongClick_description = getNearestPOI(poiItemList, latLng);
                            //System.out.println(getNearestPOI(poiItemList,latLng));
                            if (afterLongClick_description.isEmpty()) {
                                afterLongClick_description = "一个荒无人烟的地方";
                            }
                            AlarmDetail.point.setText(afterLongClick_description);
                            afterLongClick_latitude = latLng.latitude;
                            afterLongClick_longitude = latLng.longitude;
                            AlarmDetail.info.setText("未命名事件");
                            AlarmDetail.location_detail.setText(MyLatLng.showLatLngInfo(latLng));
                            AlarmDetail.range.setText("500");
                            Toast.makeText(context, "已获得位置", Toast.LENGTH_SHORT).show();
                            Marker marker = amap.addMarker(new MarkerOptions().title(afterLongClick_description)
                                    .snippet("蓝圈表示提醒范围，可以在下面自己设置").position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
                            startJumpAnimation(marker);
                            //startFlyToSkyAnimation(marker);
                            //startDropInLandAnimation(marker);
                            marker.showInfoWindow();
                            drawCircle(latLng, Double.valueOf(AlarmDetail.range.getText().toString()));
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                    }
                });
                LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500, GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);

            }

        });
    }

    public static String getNearestPOI(List<PoiItem> poiItemList, LatLng targetLocation) {
        double minDis = 500, nowDis;
        String ret = "";
        for (int i = 0; i <= poiItemList.size() - 1; i++) {
            PoiItem poiItem;
            poiItem = poiItemList.get(i);
            LatLng poilatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            nowDis = AMapUtils.calculateLineDistance(targetLocation, poilatlng);
            if (nowDis < minDis) {
                minDis = nowDis;
                ret = poiItem.toString();
            }
        }
        return ret;
    }

    public void drawDraggableMarkerWithCircle(final LatLng latLng, String title, String snippet, double radius) {
        Marker marker;
        BitmapDescriptor myicon;
        marker = amap.addMarker(new MarkerOptions().position(latLng).title("拖拽标记重设位置").snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        startGrowAnimation(marker);
        //startJumpAnimation(marker);
        marker.showInfoWindow();
        tempCircle = drawCircle(latLng, radius);
        marker.setDraggable(true);
        afterDraglatlng = latLng;
        amap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            String nearestAddr;
            String eventInfo;

            @Override
            public void onMarkerDragStart(Marker marker) {
                eventInfo=AlarmDetail.info.getText().toString();
                tempCircle.setVisible(false);
                marker.setTitle("拖拽至新的地点");
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }

            @Override
            public void onMarkerDrag(final Marker marker) {
                final LatLng latLng = marker.getPosition();
                final GeocodeSearch geocodeSearch = new GeocodeSearch(context);
                tempCircle.setCenter(marker.getPosition());
                geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                        if (i == 0) {
                            System.out.println("i=0,failed!!!!!!!!!!!!!!!!!!!!!!!!!");
                        } else {
                            List<PoiItem> poiItemList;
                            poiItemList = regeocodeResult.getRegeocodeAddress().getPois();
                            marker.setTitle(eventInfo);
                            AlarmDetail.info.setText(eventInfo);
                            marker.hideInfoWindow();
                            marker.showInfoWindow();
                            nearestAddr = getNearestPOI(poiItemList, latLng);
                            if (nearestAddr.isEmpty()) {
                                nearestAddr = "一个荒无人烟的地方";
                            }
                            marker.setSnippet(nearestAddr);
                            AlarmDetail.point.setText(nearestAddr);
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
                LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500, GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                startJumpAnimation(marker);
                AlarmDetail.point.setText(nearestAddr);
                marker.setSnippet(nearestAddr);
                tempCircle.setCenter(marker.getPosition());
                tempCircle.setVisible(true);
                //System.out.println(marker.getPosition().toString()+">>>>>>>>>>>>>>>>>>>>>>>>>>");
                afterDraglatlng = marker.getPosition();
                AlarmDetail.location_detail.setText(MyLatLng.showLatLngInfo(marker.getPosition()));
                AlarmDetail.info.setText(eventInfo);
                marker.hideInfoWindow();//刷新窗口位置
                marker.showInfoWindow();
                AlarmDetail.itemLocation = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            }
        });
    }

    public void movetoLatlng(LatLng latLng, boolean useAnimation) {
        CameraUpdate cameraUpdate;
        cameraUpdate = CameraUpdateFactory.changeLatLng(latLng);
        if (useAnimation) {
            amap.animateCamera(cameraUpdate, 1500, null);
        } else {
            amap.moveCamera(cameraUpdate);
        }
    }

    public void zoomin(float arg) {
        CameraUpdate cameraUpdate;
        cameraUpdate = CameraUpdateFactory.zoomBy(arg);
        amap.moveCamera(cameraUpdate);
    }

    public BitmapDescriptor generateBitmap(String dir, int width, int height) {
        BitmapDescriptorFactory myiconfactory;
        BitmapDescriptor myicon;
        myiconfactory = new BitmapDescriptorFactory();
        myicon = myiconfactory.fromAsset(dir);
        Bitmap bitmap, requestBitmap;
        bitmap = myicon.getBitmap();
        requestBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
        myicon = myiconfactory.fromBitmap(requestBitmap);
        return myicon;
    }

    public Circle drawCircle(LatLng latLng, double radius) {
        CircleOptions circleOptions;
        circleOptions = new CircleOptions();
        circleOptions.center(latLng).radius(radius).fillColor(0x400000ff).strokeColor(0xff0000ff).strokeWidth(2);
        tempCircle = amap.addCircle(circleOptions);
        return tempCircle;
    }

    private void startGrowAnimation(Marker growMarker) {
        if (growMarker != null) {
            Animation animation = new ScaleAnimation(0, 2, 0, 2);
            animation.setInterpolator(new LinearInterpolator());
            //整个移动所需要的时间
            animation.setDuration(2000);
            //设置动画
            growMarker.setAnimation(animation);
            //开始动画
            growMarker.startAnimation();
            animation = new ScaleAnimation(2, 1, 2, 1);
            animation.setInterpolator(new LinearInterpolator());

            animation.setDuration(1000);
            growMarker.setAnimation(animation);
            growMarker.startAnimation();
        }
    }


    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void startJumpAnimation(Marker screenMarker) {

        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = amap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(context, 125);
            LatLng target = amap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        } else {
            Log.e("ama", "screenMarker is null");
        }
    }

}
