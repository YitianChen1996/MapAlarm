package com.example.huper.mapalarm;

import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.example.huper.mapalarm.searchView.SearchFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Huper on 2017/4/9.
 */

public class AlarmDetail extends AppCompatActivity{
    MapView map = null;
    private View positiveAction;
    private EditText InfoInput;
    private String temp;
    private String city;
    private String regex;
    private Pattern pattern;
    public static MyMap myMap;
    private ImageView searchPic;
    private SearchFragment searchFragment;
    public static TextView point,range,info,location_detail;
    public static LatLng itemLocation;
    private int clickPositon;
    public static AppBarLayout myAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_detail_layout);

        map = (MapView) findViewById(R.id.map);
        CardView top = (CardView)findViewById(R.id.top);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CardView AlarmRange = (CardView) findViewById(R.id.Range);
        CardView AlarmInfo = (CardView) findViewById(R.id.Alarm_info);
        CardView AlarmPoint = (CardView) findViewById(R.id.AlarmPoint);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        MapContainer mapContainer = (MapContainer) findViewById(R.id.mapContainer);

        final TextView exit = (TextView) findViewById(R.id.exit);
        final TextView title = (TextView) findViewById(R.id.hedline);
        final CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.coll);
        final TextView save= (TextView) findViewById(R.id.save);
        final CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coor);
        myAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        searchPic = (ImageView) findViewById(R.id.search);
        point = (TextView) findViewById(R.id.point);
        info = (TextView) findViewById(R.id.TextInfo);
        range = (TextView) findViewById(R.id.RangeText);
        location_detail= (TextView) findViewById(R.id.location_detail);


        final Bundle recvbundle= this.getIntent().getExtras();
        if (recvbundle.getInt("isNewOne")==1)
        {
            map.onCreate(savedInstanceState);
            myMap=new MyMap(map.getMap(),getApplicationContext());
            myMap.startLocation(false);
            myMap.setOnLongClickListener();
//            myMap.zoomin(3);
            //System.out.println("newone==1!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        else if (recvbundle.getInt("isNewOne")==0){
            map.onCreate(savedInstanceState);
            myMap=new MyMap(map.getMap(),getApplicationContext());
            myMap.startLocation(true);
            itemLocation=new LatLng(recvbundle.getDouble("latitude"),recvbundle.getDouble("longitude"));
            myMap.movetoLatlng(itemLocation,false);
            myMap.zoomin(5);
            myMap.drawDraggableMarkerWithCircle(itemLocation, recvbundle.getString("Event_name"),recvbundle.getString("Location_des"),recvbundle.getInt("Alarm_dis"));
            point.setText(recvbundle.getString("Location_des"));
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.000");
            location_detail.setText(MyLatLng.showLatLngInfo(itemLocation));
            info.setText(recvbundle.getString("Event_name"));
            range.setText(String.valueOf(recvbundle.getInt("Alarm_dis")));
            clickPositon=recvbundle.getInt("clickPosition");
        }

        mapContainer.setScrollView(mCollapsingToolbarLayout);
        mCollapsingToolbarLayout.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        //TODO,获取当前城市并传递给city，将西安替换为city。
        //city = amapLocation.getCity();;

        searchPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFragment = SearchFragment.newInstance(myMap.currentCityName);
                searchFragment.show(getSupportFragmentManager(), SearchFragment.TAG);
            }
        });

        AlarmInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(AlarmDetail.this)
                        .title("备注")
                        .titleGravity(GravityEnum.CENTER)
                        .customView(R.layout.edit_info,true)
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                InfoInput = (EditText) dialog.getCustomView().findViewById(R.id.EditInfo);
                                info.setText(InfoInput.getText().toString());
                            }
                        })
                        .build();
                    positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                    InfoInput = (EditText) dialog.getCustomView().findViewById(R.id.EditInfo);
                    InfoInput.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            positiveAction.setEnabled(s.toString().trim().length() > 0);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    dialog.show();
                    positiveAction.setEnabled(false);
                    positiveAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            info.setText(InfoInput.getText().toString());
                            dialog.dismiss();
                        }
                    });
            }
        });

        AlarmRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(AlarmDetail.this)
                        .title("提醒范围(米)")
                        .titleGravity(GravityEnum.CENTER)
                        .customView(R.layout.edit_range,true)
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                InfoInput = (EditText) dialog.getCustomView().findViewById(R.id.EditRange);
                                range.setText(InfoInput.getText().toString());
                            }
                        })
                        .build();
                positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                InfoInput = (EditText) dialog.getCustomView().findViewById(R.id.EditRange);
                InfoInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        regex = "^[0-9]*[1-9][0-9]*$";
                        pattern = Pattern.compile(regex);
                        Matcher match = pattern.matcher(s.toString().trim());
                        positiveAction.setEnabled(s.toString().trim().length() > 0 && match.matches());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                dialog.show();
                positiveAction.setEnabled(false);
                positiveAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        range.setText(InfoInput.getText().toString());
                        if (recvbundle.getInt("isNewOne")==1)
                        {
                            myMap.amap.clear();
                            myMap.amap.addMarker(new MarkerOptions().title(myMap.afterLongClick_description)
                                    .snippet("蓝圈表示提醒范围，可以在下面自己设置").position(new LatLng(myMap.afterLongClick_latitude,myMap.afterLongClick_longitude))).showInfoWindow();
                            myMap.drawCircle(new LatLng(myMap.afterLongClick_latitude,myMap.afterLongClick_longitude),Double.valueOf(range.getText().toString()));
                        }else if (recvbundle.getInt("isNewOne")==0){
                            myMap.tempCircle.remove();
                            myMap.drawCircle(new LatLng(itemLocation.latitude,itemLocation.longitude),Double.valueOf(range.getText().toString()));
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

       AlarmPoint.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final MaterialDialog dialog = new MaterialDialog.Builder(AlarmDetail.this)
                       .title("设置提醒地点")
                       .titleGravity(GravityEnum.CENTER)
                       .customView(R.layout.edit_place,true)
                       .positiveText("确定")
                       .negativeText("取消")
                       .onPositive(new MaterialDialog.SingleButtonCallback() {
                           @Override
                           public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               InfoInput = (EditText) dialog.getCustomView().findViewById(R.id.EditPlace);
                               info.setText(InfoInput.getText().toString());
                           }
                       })
                       .build();
               positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
               InfoInput = (EditText) dialog.getCustomView().findViewById(R.id.EditPlace);
               InfoInput.setText(point.getText().toString());
               InfoInput.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                   }
                   @Override
                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                       positiveAction.setEnabled(s.toString().trim().length() > 0);
                   }
                   @Override
                   public void afterTextChanged(Editable s) {
                   }
               });
               dialog.show();
               positiveAction.setEnabled(true);
               positiveAction.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       point.setText(InfoInput.getText().toString());
                       dialog.dismiss();
                   }
               });
           }
       });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAppBarLayout.setExpanded(true);
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(AlarmDetail.this)
                        .content("确认要退出吗？")
                        .positiveText("退出")
                        .negativeText("留下")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                setResult(3);
                                finish();
                            }
                        })
                        .show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((myMap.afterLongClick_latitude<0||myMap.afterLongClick_longitude<0)&&(recvbundle.getInt("isNewOne")==1)){
                    Toast.makeText(getApplicationContext(),"未在地图上选择地点，不能保存",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (MainActivity.db.findID(info.getText().toString(),point.getText().toString())!=-1&&
                        MainActivity.db.findID(clickPositon)!=MainActivity.db.findID(info.getText().toString(),point.getText().toString())){
                    MaterialDialog dialog = new MaterialDialog.Builder(AlarmDetail.this)
                            .content("列表中已存在一个名称和地点完全相同的事件\n请重新输入")
                            .positiveText("确认")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return;
                }
                Bundle retbundle=new Bundle();
                retbundle.putString("Location_des",point.getText().toString());
                retbundle.putString("Event_name",info.getText().toString());
                retbundle.putInt("Alarm_dis",Integer.valueOf(range.getText().toString()));
                Intent retintent=new Intent();
                if (recvbundle.getInt("isNewOne")==1) {
                    retbundle.putDouble("latitude",myMap.afterLongClick_latitude);
                    retbundle.putDouble("longitude",myMap.afterLongClick_longitude);
                    setResult(1,retintent);
                }
                else{
                    retbundle.putDouble("latitude",myMap.afterDraglatlng.latitude);
                    retbundle.putDouble("longitude",myMap.afterDraglatlng.longitude);
                    retbundle.putInt("clickID",recvbundle.getInt("clickID"));
                    retbundle.putInt("clickPosition",recvbundle.getInt("clickPosition"));
                    MainActivity.alarmDatas.get(recvbundle.getInt("clickPosition")).nowdis=-1;
                    setResult(2,retintent);
                }
                retintent.putExtras(retbundle);
                finish();
            }
        });

        myAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRangle = appBarLayout.getTotalScrollRange();
                if (verticalOffset >= -0.81*scrollRangle) {
                    exit.setTextColor(getResources().getColorStateList(R.color.text_selecter_2));
                    save.setTextColor(getResources().getColorStateList(R.color.text_selecter_2));
                    title.setTextColor(Color.parseColor("#616161"));
                    searchPic.setImageResource(R.drawable.ic_search_black_24dp);
                } else {
                    exit.setTextColor(getResources().getColorStateList(R.color.text_selecter));
                    save.setTextColor(getResources().getColorStateList(R.color.text_selecter));
                    title.setTextColor(Color.parseColor("#F5F5F5"));
                    searchPic.setImageResource(R.drawable.ic_search_white_24dp);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAppBarLayout.setExpanded(false);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause();
    }
    @Override
    public void onBackPressed() {
        MaterialDialog dialog = new MaterialDialog.Builder(AlarmDetail.this)
                .content("确认要退出吗？")
                .positiveText("退出")
                .negativeText("留下")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }



}
