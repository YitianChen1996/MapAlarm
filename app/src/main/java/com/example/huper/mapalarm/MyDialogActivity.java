package com.example.huper.mapalarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by dell on 2017/4/18.
 */

public class MyDialogActivity extends Activity {
    Button bt1;
    Button bt2;

    NotificationManager nm;
    PowerManager.WakeLock mWakelock;
    Media media;
    String Text;
    public Bundle recvbundle;
    protected void onCreate(Bundle dfs) {
        super.onCreate(dfs);
        recvbundle=new Bundle();
        recvbundle=this.getIntent().getExtras();
        String ns = Context.NOTIFICATION_SERVICE;
        nm = (NotificationManager)this.getSystemService(ns);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        media = new Media(this);
        media.play();
        createDialog();
    }
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock();
        // 唤醒屏幕
    }


    /**
     * 唤醒屏幕
     */
    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    private void createDialog() {
        new MaterialDialog.Builder(MyDialogActivity.this)
                .title("来自MapAlarm的提醒")
                .icon(getResources().getDrawable(R.mipmap.logo1))
                .maxIconSize(60)
                .content("亲，你已到达"+recvbundle.getString("location")+"附近!\n"+"事件名称："+recvbundle.getString("eventname"))
                .cancelable(false)
                .positiveText("关闭提醒")
                .negativeText("保留提醒")
                .positiveColor(Color.parseColor("#8BC34A"))
                .negativeColor(Color.parseColor("#8BC34A"))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        nm.cancel(101);
                        media.pause();
                        //数据库关闭
                        MainActivity.db.setOffAlarm(recvbundle.getInt("databaseID"));

                        //alarmDatas关闭state，alarmedThisTime依然关闭
                        MainActivity.alarmDatas.get(recvbundle.getInt("position")).state=false;
                        MainActivity.myAdapter.notifyItemChanged(recvbundle.getInt("position"));
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        nm.cancel(101);
                        media.pause();
                        //System.out.println("222222222222222222222222222222222222");
                        //数据库不必动，依然打开
                        MainActivity.alarmDatas.get(recvbundle.getInt("position")).alarmedThisTime=true;
                        finish();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        media.pause();
                        finish();
                    }
                }).show();
    }
}
