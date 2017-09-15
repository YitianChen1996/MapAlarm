package com.example.huper.mapalarm;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Huper on 2017/4/22.
 */

public class Token {
    public static final String IS_INSTALLED = "token";
    public static final String APP_ID = "com.example.huper.mapalarm";

    public static boolean check_is_installed(Context context){
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getBoolean(IS_INSTALLED, false);
    }

    public static void write_token(Context context,boolean token){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_INSTALLED,token);
        editor.commit();
    }

    public static void clear(Context context){
        context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit().clear().commit();
    }
}
