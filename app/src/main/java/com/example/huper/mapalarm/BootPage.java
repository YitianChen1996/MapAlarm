package com.example.huper.mapalarm;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.sacot41.scviewpager.DotsView;
import com.dev.sacot41.scviewpager.SCPositionAnimation;
import com.dev.sacot41.scviewpager.SCViewAnimation;
import com.dev.sacot41.scviewpager.SCViewAnimationUtil;
import com.dev.sacot41.scviewpager.SCViewPager;
import com.dev.sacot41.scviewpager.SCViewPagerAdapter;

/**
 * Created by Huper on 2017/4/19.
 */

public class BootPage extends AppCompatActivity {
    private static final int NUM_PAGES = 5;

    private SCViewPager mViewPager;
    private SCViewPagerAdapter mPageAdapter;
    private DotsView mDotsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.boot_layout);

        mViewPager = (SCViewPager) findViewById(R.id.BootPage);
        mDotsView = (DotsView) findViewById(R.id.dotsview_main);
        mDotsView.setDotRessource(R.drawable.dot_selected, R.drawable.dot_unselected);
        mDotsView.setNumberOfPage(NUM_PAGES);

        mPageAdapter = new SCViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.setNumberOfPage(NUM_PAGES);
        mPageAdapter.setFragmentBackgroundColor(R.color.white);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mDotsView.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        final Point size = SCViewAnimationUtil.getDisplaySize(this);

        View hi = findViewById(R.id.hi_text);
        ((TextView)hi).setTypeface(Typeface.createFromAsset(getAssets(),"Satisfy-Regular.ttf"));
        SCViewAnimation hiAnimation = new SCViewAnimation(hi);
        hiAnimation.addPageAnimation(new SCPositionAnimation(this, 0,0,-size.y));
        mViewPager.addAnimation(hiAnimation);

        View nice_to_meet = findViewById(R.id.nice_to_meet);
        SCViewAnimation meetAnimation = new SCViewAnimation(nice_to_meet);
        meetAnimation.addPageAnimation(new SCPositionAnimation(this, 0,0,size.y));
        mViewPager.addAnimation(meetAnimation);

        View alarm = findViewById(R.id.clock1);
        SCViewAnimation alarmAnimation = new SCViewAnimation(alarm);
        alarmAnimation.startToPosition((int)(size.x*1.5),null);
        alarmAnimation.addPageAnimation(new SCPositionAnimation(this, 0,(int)(-size.x*1.5),0));
        alarmAnimation.addPageAnimation(new SCPositionAnimation(this, 1,-size.x,0));
        mViewPager.addAnimation(alarmAnimation);

        View  first = findViewById(R.id.first_alarm);
        SCViewAnimation firstAnimation = new SCViewAnimation(first);
        firstAnimation.startToPosition((int)(size.x*1.5),null);
        firstAnimation.addPageAnimation(new SCPositionAnimation(this, 0,(int)(-size.x*1.5),0));
        firstAnimation.addPageAnimation(new SCPositionAnimation(this, 1,(int)(-size.x*1.5),0));
        mViewPager.addAnimation(firstAnimation);

        View until = findViewById(R.id.until_today);
        SCViewAnimation untilAnimation = new SCViewAnimation(until);
        untilAnimation.startToPosition((int)(size.x*1.5),null);
        untilAnimation.addPageAnimation(new SCPositionAnimation(this, 0,(int)(-size.x*1.5),0));
        untilAnimation.addPageAnimation(new SCPositionAnimation(this, 1,-size.x*2,0));
        mViewPager.addAnimation(untilAnimation);

        View usage = findViewById(R.id.usage_of_alarm);
        SCViewAnimation usageAnimation = new SCViewAnimation(usage);
        usageAnimation.startToPosition((int)(size.x*1.5),null);
        usageAnimation.addPageAnimation(new SCPositionAnimation(this, 0,(int)(-size.x*1.5),0));
        usageAnimation.addPageAnimation(new SCPositionAnimation(this, 1,(int)(-size.x*1.5),0));
        mViewPager.addAnimation(usageAnimation);

        View time = findViewById(R.id.do_on_time);
        SCViewAnimation timeAnimation = new SCViewAnimation(time);
        timeAnimation.startToPosition((int)(size.x*1.5),null);
        timeAnimation.addPageAnimation(new SCPositionAnimation(this, 0,(int)(-size.x*1.5),0));
        timeAnimation.addPageAnimation(new SCPositionAnimation(this, 1,-size.x*2,0));
        mViewPager.addAnimation(timeAnimation);

        View building = findViewById(R.id.building);
        SCViewAnimation buildingAnimation = new SCViewAnimation(building);
        buildingAnimation.startToPosition(null,-size.y);
        buildingAnimation.addPageAnimation(new SCPositionAnimation(this, 1,0,size.y));
        buildingAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,-size.y));
        mViewPager.addAnimation(buildingAnimation);

        View but = findViewById(R.id.but);
        SCViewAnimation butAnimation = new SCViewAnimation(but);
        butAnimation.startToPosition(null,size.y);
        butAnimation.addPageAnimation(new SCPositionAnimation(this, 1,0,-size.y));
        butAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,size.y));
        mViewPager.addAnimation(butAnimation);

        View want = findViewById(R.id.want);
        SCViewAnimation wantAnimation = new SCViewAnimation(want);
        wantAnimation.startToPosition(null,size.y);
        wantAnimation.addPageAnimation(new SCPositionAnimation(this, 1,0,-size.y));
        wantAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,size.y));
        mViewPager.addAnimation(wantAnimation);

        View then = findViewById(R.id.then);
        SCViewAnimation thenAnimation = new SCViewAnimation(then);
        thenAnimation.startToPosition(null,size.y);
        thenAnimation.addPageAnimation(new SCPositionAnimation(this, 1,0,-size.y));
        thenAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,size.y));
        mViewPager.addAnimation(thenAnimation);

        View invent = findViewById(R.id.invent);
        ((TextView)invent).setTypeface(Typeface.createFromAsset(getAssets(),"Satisfy-Regular.ttf"));
        SCViewAnimation inventAnimation = new SCViewAnimation(invent);
        inventAnimation.startToPosition(null,size.y);
        SCViewAnimationUtil.prepareViewToGetSize(invent);
        inventAnimation.addPageAnimation(new SCPositionAnimation(this, 1,0,-size.y));
        inventAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,-(size.y/2+invent.getHeight())));
        inventAnimation.addPageAnimation(new SCPositionAnimation(this, 3,-size.x,0));
        mViewPager.addAnimation(inventAnimation);

        View help = findViewById(R.id.help);
        SCViewAnimation helpAnimation = new SCViewAnimation(help);
        helpAnimation.startToPosition(null,size.y);
        helpAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,-size.y));
        helpAnimation.addPageAnimation(new SCPositionAnimation(this, 3,0,size.y));
        mViewPager.addAnimation(helpAnimation);

        View yes = findViewById(R.id.yes);
        SCViewAnimation yesAnimation = new SCViewAnimation(yes);
        yesAnimation.startToPosition(null,size.y);
        yesAnimation.addPageAnimation(new SCPositionAnimation(this, 2,0,-size.y));
        yesAnimation.addPageAnimation(new SCPositionAnimation(this, 3,0,size.y));
        mViewPager.addAnimation(yesAnimation);

        View enjoy = findViewById(R.id.enjoy);
        ((TextView)enjoy).setTypeface(Typeface.createFromAsset(getAssets(),"Satisfy-Regular.ttf"));
        SCViewAnimation enjoyAnimation = new SCViewAnimation(enjoy);
        enjoyAnimation.startToPosition(size.x,null);
        enjoyAnimation.addPageAnimation(new SCPositionAnimation(this, 3,-size.x,0));
        mViewPager.addAnimation(enjoyAnimation);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean flag;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch(state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        flag = false;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        flag = true;
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        if(mViewPager.getCurrentItem() == mPageAdapter.getCount() - 1 && !flag) {
                            if(Token.check_is_installed(BootPage.this)) {
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            else{
                                startActivity(new Intent(BootPage.this,MainActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                Token.write_token(BootPage.this,true);
                                finish();
                            }
                        }
                        flag = true;
                        break;
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
