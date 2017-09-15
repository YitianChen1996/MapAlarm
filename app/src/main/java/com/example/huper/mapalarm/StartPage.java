package com.example.huper.mapalarm;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by Huper on 2017/4/22.
 */

public class StartPage extends AppCompatActivity{
    boolean is_installed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_layout);

        is_installed = Token.check_is_installed(this);
        ShimmerTextView Logo = (ShimmerTextView) findViewById(R.id.Logo);

        Logo.setTypeface(Typeface.createFromAsset(getAssets(),"Satisfy-Regular.ttf"));
        final Shimmer shimmer = new Shimmer();
        shimmer.start(Logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                shimmer.cancel();
                choose();
            }
        },2500);
    }

    public void choose(){
        if(!is_installed){
            startActivity(new Intent(this,BootPage.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        else{
            startActivity(new Intent(this,MainActivity.class));
        }
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
