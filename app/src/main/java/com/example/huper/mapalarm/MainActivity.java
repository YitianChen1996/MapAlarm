package com.example.huper.mapalarm;

    import android.Manifest;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SyncStatusObserver;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.graphics.Color;
    import android.graphics.drawable.BitmapDrawable;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.provider.Settings;
    import android.support.annotation.NonNull;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.NavigationView;
    import android.support.design.widget.Snackbar;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.content.ContextCompat;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.DefaultItemAnimator;
    import android.support.v7.widget.LinearLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;
    import android.view.Gravity;
    import android.view.KeyEvent;
    import android.view.LayoutInflater;
    import android.view.MotionEvent;
    import android.view.View;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.WindowManager;
    import android.view.animation.Animation;
    import android.view.animation.AnimationUtils;
    import android.webkit.WebView;
    import android.widget.AdapterView;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.ListView;
    import android.widget.PopupWindow;
    import android.widget.RelativeLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.afollestad.materialdialogs.DialogAction;
    import com.afollestad.materialdialogs.GravityEnum;
    import com.afollestad.materialdialogs.MaterialDialog;
    import com.amap.api.location.AMapLocationClientOption;
    import com.amap.api.maps.AMap;

    import java.lang.reflect.Method;
    import java.util.ArrayList;
    import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopupWindow.OnDismissListener{

        public static MyAdapter myAdapter;
        public static List<AlarmData> alarmDatas = new ArrayList<AlarmData>();
        private PopupWindow popupWindow;
        private RecyclerView alarmList;
        private int temp_position;
        public static Database db;
        public static BackgroundLocation backgroundLocation;
        private boolean started;
        private ImageView clickMe;
        private Animation clickAnim;
        private LinearLayout noInfo;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent PermissionCheck=new Intent(getApplicationContext(),PermissionCheck.class);
        if(isNeedCheck){
            checkPermissions(needPermissions);
        }
        //startActivityForResult(PermissionCheck,1001);
        db=new Database(getApplicationContext());

//        if (db.getTotalEventNum()==0){
//            Intent intent=new Intent(getApplicationContext(),BootPage.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        }

        setContentView(R.layout.activity_main);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                mDrawerLayout.setFitsSystemWindows(true);
                mDrawerLayout.setClipToPadding(false);
            }
        }

        clickMe = (ImageView) findViewById(R.id.new_alarm_pic);
        noInfo = (LinearLayout) findViewById(R.id.no_alarm_info);
        LinearLayout  cover = (LinearLayout) findViewById(R.id.cover);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        fillalarmDatas();

        alarmList = (RecyclerView) findViewById(R.id.AlarmList);
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");

//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服1",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服2",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服3",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服4",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服5",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服6",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服7",true));
//        alarmDatas.add(new AlarmData("星天苑D座一楼洗衣房","取衣服8",true));

        myAdapter = new MyAdapter(this,alarmDatas);
        alarmList.setLayoutManager(new LinearLayoutManager(this));
        ((DefaultItemAnimator)alarmList.getItemAnimator()).setSupportsChangeAnimations(false);
        alarmList.setAdapter(myAdapter);
        alarmList.addItemDecoration(new CustomItemDecoration(this));

        check_num();
        cover.setOnClickListener(null);
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent_AlarmDetail=new Intent(MainActivity.this,AlarmDetail.class);
                Bundle sendbundle=new Bundle();
                int clickID=db.findID(alarmDatas.get(position).todo,alarmDatas.get(position).address);
                sendbundle.putInt("clickID",clickID);
                if (clickID==-1){
                    Toast.makeText(getApplicationContext(),"错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                sendbundle.putInt("isNewOne",0);
                sendbundle.putString("Location_des",alarmDatas.get(position).address);
                sendbundle.putString("Event_name",alarmDatas.get(position).todo);
                sendbundle.putDouble("latitude",alarmDatas.get(position).latitude);
                sendbundle.putDouble("longitude",alarmDatas.get(position).longitude);
                sendbundle.putInt("Alarm_dis",alarmDatas.get(position).alarmDis);
                sendbundle.putInt("clickPosition",position);
                intent_AlarmDetail.putExtras(sendbundle);

                startActivityForResult(intent_AlarmDetail,1000);
            }
        });
        myAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                openPopupWindow(view);
                temp_position = position;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle sendbundle=new Bundle();
                sendbundle.putInt("isNewOne",1);
                Intent intent_AlarmDetail;
                intent_AlarmDetail = new Intent(getApplicationContext(), AlarmDetail.class);
                intent_AlarmDetail.putExtras(sendbundle);
                startActivityForResult(intent_AlarmDetail, 1000);
                //startActivity(new Intent(MainActivity.this,AlarmDetail.class));
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.choose_locate :{
                    new MaterialDialog.Builder(MainActivity.this)
                                .title("请选择定位方式：")
                                .items(R.array.locate_values)
                                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        switch (which){
                                            case 0: {
                                                backgroundLocation.stopLocation();
                                                backgroundLocation.mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
                                                backgroundLocation.startLocation();
                                                break;
                                            }
                                            case 1:{
                                                backgroundLocation.stopLocation();
                                                backgroundLocation.mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                                                backgroundLocation.startLocation();
                                                break;
                                            }
                                            case 2:{
                                                backgroundLocation.stopLocation();
                                                backgroundLocation.mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                                                backgroundLocation.startLocation();
                                                break;
                                            }
                                        }
                                        Toast.makeText(getApplicationContext(),"设置成功,下次定位起生效",Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                })
                                .positiveText("确定")
                                .show();
                        break;
                    }
                    case R.id.choose_boot:{
                        Intent intent=new Intent(MainActivity.this,BootPage.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case R.id.choose_intro:{
                        startActivity(new Intent(MainActivity.this,IntroActivity.class));
                        break;
                    }
                    case R.id.choose_team:{
                       new MaterialDialog.Builder(MainActivity.this)
                                .title("团队信息")
                                .titleGravity(GravityEnum.CENTER)
                                .customView(R.layout.team_layout,true)
                                .negativeText("已阅")
                                .show();
                        break;
                    }
                }
                return false;
            }
        });
        backgroundLocation=new BackgroundLocation(getApplicationContext());
        backgroundLocation.startLocation();
        if (BackgroundLocation.getActiveEventNum()==0){
            backgroundLocation.stopLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1000){
            if (resultCode!=1&&resultCode!=2){//点击取消
                return;
            }
            Bundle recvbundle = data.getExtras();
            if (resultCode == 1) {//如果是新建事件的返回
                if (db.insertData(recvbundle)!=-1){
                    Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                    alarmDatas.add(new AlarmData(recvbundle.getString("Location_des"), recvbundle.getString("Event_name"), true,
                            recvbundle.getDouble("latitude"),recvbundle.getDouble("longitude"),recvbundle.getInt("Alarm_dis")));
//                    myAdapter.notifyItemInserted(alarmDatas.size()-1);
                    //System.out.println(BackgroundLocation.getActiveEventNum()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                }else{
                    Toast.makeText(getApplicationContext(),"保存失败，禁止两个名称和地点完全相同的事件",Toast.LENGTH_SHORT).show();
                };
            }
            if (resultCode==2) {//如果是修改事件的返回
                //升级数据库
                if (db.updateAllInfo(recvbundle.getInt("clickID"),recvbundle,recvbundle.getInt("clickPosition"))) {
                    //修改列表值
                    int clickPosition = recvbundle.getInt("clickPosition");
                    alarmDatas.get(clickPosition).address = recvbundle.getString("Location_des");
                    alarmDatas.get(clickPosition).todo = recvbundle.getString("Event_name");
                    alarmDatas.get(clickPosition).alarmDis = recvbundle.getInt("Alarm_dis");
                    alarmDatas.get(clickPosition).latitude = recvbundle.getDouble("latitude");
                    alarmDatas.get(clickPosition).longitude = recvbundle.getDouble("longitude");
                    alarmDatas.get(clickPosition).state = true;
                    myAdapter.notifyItemChanged(clickPosition);
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    //System.out.println(BackgroundLocation.getActiveEventNum()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                }else{
                    Toast.makeText(getApplicationContext(),"保存失败，禁止两个名称和地点完全相同的事件",Toast.LENGTH_SHORT).show();
                }
            }
            if (BackgroundLocation.getActiveEventNum()>=1){
                MainActivity.backgroundLocation.startLocation();
            }
            check_num();
        }else if (requestCode==1001){
            if (resultCode==0){
                Toast.makeText(getApplicationContext(),"缺少必要权限，可能无法正常使用",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconsVisible(menu,true);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_info) {
            new MaterialDialog.Builder(MainActivity.this)
                    .title("团队信息")
                    .titleGravity(GravityEnum.CENTER)
                    .customView(R.layout.team_layout,true)
                    .negativeText("已阅")
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPopupWindow(View v) {

        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.pop_window_layout, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        popupWindow.showAtLocation(v,Gravity.BOTTOM,0,0);
        popupWindow.setOnDismissListener(this);
        setOnPopupViewClick(view);
        setBackgroundAlpha(0.5f);
    }

    private void setOnPopupViewClick(View view) {
        TextView delete_alarm, cancel;
        delete_alarm = (TextView) view.findViewById(R.id.delete_alarm);
        cancel = (TextView) view.findViewById(R.id.cancel);
        delete_alarm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_alarm:
                if (db.deleteFromDatabase(temp_position,alarmDatas)){
                    Toast.makeText(this, "成功删除！", Toast.LENGTH_SHORT).show();
                    alarmDatas.remove(temp_position);
                    myAdapter.notifyItemRemoved(temp_position);
                    if (BackgroundLocation.getActiveEventNum()==0){
                        MainActivity.backgroundLocation.stopLocation();
                    }
                    //System.out.println(BackgroundLocation.getActiveEventNum()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    check_num();
                    popupWindow.dismiss();
                    break;
                }else {
                    Toast.makeText(this, "删除失败！", Toast.LENGTH_SHORT).show();
                }

            case R.id.cancel:
                popupWindow.dismiss();
                break;
        }
    }

    public void fillalarmDatas(){
        Cursor c=db.queryDatabase();
        alarmDatas.clear();
        while(c.moveToNext()){
            boolean temp;
            if (c.getInt(c.getColumnIndex("isActive"))!=0) {
                temp=true;
            }else {
                temp=false;
            }
            alarmDatas.add(new AlarmData(c.getString(c.getColumnIndex("EventLocationDes")), c.getString(c.getColumnIndex("EventName")),temp,
                    c.getDouble(c.getColumnIndex("latitude")),c.getDouble(c.getColumnIndex("longitude")),c.getInt(c.getColumnIndex("alarmDis"))));
        }
        db.dbRead.close();
        c.close();
    }

    public void check_num(){
        if(db.getTotalEventNum() == 0){
            startAnimation();
            clickMe.setVisibility(View.VISIBLE);
            noInfo.setVisibility(View.VISIBLE);
        }
        else{
            stopAnimation();
            clickMe.setVisibility(View.GONE);
            noInfo.setVisibility(View.GONE);
        }
    }

    public void stopAnimation(){
        started=false;
        clickMe.clearAnimation();
    }

    public void startAnimation(){
        started=true;
        clickMe.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                if(started)
                    ClickMeAnimation();
            }
        },500);
    }

    private void ClickMeAnimation(){
        clickMe.setVisibility(View.VISIBLE);
        clickAnim = AnimationUtils.loadAnimation(this,R.anim.message_launcher);
        clickMe.startAnimation(clickAnim);
        clickAnim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                if(started)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run(){
                            if(started)
                                ClickMeAnimation();
                        }
                    },2000);
            }
        });
    }

    private void setIconsVisible(Menu menu, boolean flag) {
        if(menu != null) {
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long lastclicktime = 0;

    @Override
    public void onBackPressed() {
        if (lastclicktime <= 0) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            lastclicktime = System.currentTimeMillis();
        } else {
            long currentclicktime = System.currentTimeMillis();
            if (currentclicktime - lastclicktime < 1500) {
                finish();
            } else {
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                lastclicktime = System.currentTimeMillis();
            }
        }
    }


    /**
     *
     * @param
     * @since 2.5.0
     * requestPermissions方法是请求某一权限，
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     * checkSelfPermission方法是在用来判断是否app已经获取到某一个权限
     * shouldShowRequestPermissionRationale方法用来判断是否
     * 显示申请权限对话框，如果同意了或者不在询问则返回false
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     * @param grantResults
     * @return
     * @since 2.5.0
     *
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限结果的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                System.out.println(permissions+">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                showMissingPermissionDialog();
                isNeedCheck = false;
            }else{
                //finish();
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     *
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     *  启动应用的设置
     *
     * @since 2.5.0
     *
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
