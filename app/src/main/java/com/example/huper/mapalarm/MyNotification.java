package com.example.huper.mapalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

/**
 * Created by chl on 2017/4/17.
 */

public class MyNotification {
    Context context;
    String ns = Context.NOTIFICATION_SERVICE;
    NotificationManager mNotificationManager;
    String temp1;
    String temp2;
    public MyNotification(Context context, String temp1, String temp2)
    {
        this.context = context;
        mNotificationManager = (NotificationManager) context.getSystemService(ns);
    }


    CharSequence tickerText = "事件通知";
    //定义下拉通知栏时要展现的内容信息

    public void display() {
        CharSequence contentTitle = "你已经触发事件";
        CharSequence contentText = temp2;
        CharSequence contentinfo = temp1;
        long when = System.currentTimeMillis();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification.Builder b = new Notification.Builder(context)
                .setAutoCancel(true)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
                .setWhen(when)
                .setContentInfo(contentinfo);
        Notification n = b.getNotification();
        mNotificationManager.notify(101, n);
    }
}
