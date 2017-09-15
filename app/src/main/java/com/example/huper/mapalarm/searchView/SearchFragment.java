package com.example.huper.mapalarm.searchView;

/**
 * Created by Huper on 2017/5/15.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.huper.mapalarm.AlarmDetail;
import com.example.huper.mapalarm.CustomItemDecoration;
import com.example.huper.mapalarm.MyMap;
import com.example.huper.mapalarm.R;
import com.example.huper.mapalarm.searchView.adapter.LocationBean;
import com.example.huper.mapalarm.searchView.adapter.SearchHistoryAdapter;
import com.example.huper.mapalarm.searchView.adapter.SearchTipsAdapter;
import com.example.huper.mapalarm.searchView.custom.CircularRevealAnim;
import com.example.huper.mapalarm.searchView.custom.IOnItemClickListener;
import com.example.huper.mapalarm.searchView.db.SearchHistoryDB;
import com.example.huper.mapalarm.searchView.utils.KeyBoardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Won on 2017/1/13.
 */

public class SearchFragment extends DialogFragment implements View.OnTouchListener,TextWatcher, Inputtips.InputtipsListener, DialogInterface.OnKeyListener, ViewTreeObserver.OnPreDrawListener, CircularRevealAnim.AnimListener, View.OnClickListener {

    public static final String TAG = "SearchFragment";
    private String city;
    private ImageView ivSearchBack;
    private EditText etSearchKeyword;
    private RecyclerView rvSearchList;
    private View searchUnderline;
    private TextView tvSearchClean;
    private TextView deleatText;
    private View viewSearchOutside;
    private SearchTipsAdapter searchTipsAdapter;
    private ImageView clearPic;
    AMap aMap;

    private View view;
    //动画
    private CircularRevealAnim mCircularRevealAnim;
    //历史搜索记录
    private ArrayList<LocationBean> allHistorys = new ArrayList<>();
    private ArrayList<LocationBean> historys = new ArrayList<>();
    //适配器
    private SearchHistoryAdapter searchHistoryAdapter;
    //数据库
    private SearchHistoryDB searchHistoryDB;

    public static SearchFragment newInstance(String locate) {
        Bundle bundle = new Bundle();
        bundle.putString("city", locate);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_search, container, false);
        init();//实例化
        return view;
    }

    private void init() {
        ivSearchBack = (ImageView) view.findViewById(R.id.iv_search_back);
        etSearchKeyword = (EditText) view.findViewById(R.id.et_search_keyword);
        rvSearchList = (RecyclerView) view.findViewById(R.id.rv_search_list);
        searchUnderline = (View) view.findViewById(R.id.search_underline);
        deleatText = (TextView) view.findViewById(R.id.tv_search_clean);
        tvSearchClean = (TextView) view.findViewById(R.id.tv_search_clean);
        viewSearchOutside = (View) view.findViewById(R.id.view_search_outside);
        clearPic = (ImageView) view.findViewById(R.id.iv_delete_text);

        //实例化动画效果
        mCircularRevealAnim = new CircularRevealAnim();
        //监听动画
        mCircularRevealAnim.setAnimListener(this);
        getDialog().setOnKeyListener(this);//键盘按键监听
        clearPic.getViewTreeObserver().addOnPreDrawListener(this);//绘制监听

        //实例化数据库
        searchHistoryDB = new SearchHistoryDB(getContext(), SearchHistoryDB.DB_NAME, null, 1);
        allHistorys = searchHistoryDB.queryAllHistory();
        setAllHistorys();
        //初始化recyclerView
        rvSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchList.addItemDecoration(new CustomItemDecoration(getActivity()));
        rvSearchList.setOnTouchListener(this);
        searchHistoryAdapter = new SearchHistoryAdapter(getContext(), historys);
        rvSearchList.setAdapter(searchHistoryAdapter);
        //设置删除单个记录的监听
        searchHistoryAdapter.setOnItemClickListener(new IOnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                hideAnim();
                AlarmDetail.myMap.movetoLatlng(new LatLng(historys.get(position).getPoint().getLatitude(),
                        historys.get(position).getPoint().getLongitude()),
                        true);
            }

            @Override
            public void onItemDeleteClick(int position) {
                searchHistoryDB.deleteHistory(historys.get(position));
                historys.remove(historys.get(position));
                checkHistorySize();
                searchHistoryAdapter.notifyDataSetChanged();
            }
        });
        etSearchKeyword.addTextChangedListener(this);
        ivSearchBack.setOnClickListener(this);
        viewSearchOutside.setOnClickListener(this);
        clearPic.setOnClickListener(this);
        tvSearchClean.setOnClickListener(this);
        clearPic.setVisibility(View.GONE);
        if (getArguments() != null)
            city = getArguments().getString("city");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_search_back || view.getId() == R.id.view_search_outside) {
            hideAnim();
        } else if (view.getId() == R.id.iv_delete_text) {
            etSearchKeyword.setText("");
            clearPic.setVisibility(View.GONE);
        } else if (view.getId() == R.id.tv_search_clean) {
            searchHistoryDB.deleteAllHistory();
            historys.clear();
            searchUnderline.setVisibility(View.GONE);
            deleatText.setVisibility(View.GONE);
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void initDialog() {
        Window window = getDialog().getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.98); //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.DialogEmptyAnimation);//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            hideAnim();
        return false;
    }

    @Override
    public boolean onPreDraw() {
        clearPic.getViewTreeObserver().removeOnPreDrawListener(this);
        mCircularRevealAnim.show(clearPic, view);
        return true;
    }

    @Override
    public void onHideAnimationEnd() {
        etSearchKeyword.setText("");
        dismiss();
    }

    @Override
    public void onShowAnimationEnd() {
        if (isVisible()) {
            KeyBoardUtils.openKeyboard(getContext(), etSearchKeyword);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!newText.equals("")) {
            //当前输入不为空，查询。
            clearPic.setVisibility(View.VISIBLE);
            InputtipsQuery inputquery = new InputtipsQuery(newText, city);
            inputquery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(getActivity(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        } else {
            //当前输入为空，适配历史记录。
            clearPic.setVisibility(View.GONE);
            rvSearchList.setAdapter(searchHistoryAdapter);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String newText = editable.toString().trim();
        if(newText.equals("")){
            clearPic.setVisibility(View.GONE);
            rvSearchList.setAdapter(searchHistoryAdapter);
        }
    }

    @Override
    public void onGetInputtips(final List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS && !etSearchKeyword.getText().toString().trim().equals("")) {
            //查询结果不为空，适配数据。
            final ArrayList<LocationBean> alltips = new ArrayList<>();
            for (int i = 0; i < tipList.size(); i++){
                if(tipList.get(i).getPoint()!=null)
                    alltips.add(new LocationBean(tipList.get(i).getName(), tipList.get(i).getAddress(),tipList.get(i).getPoint()));
            }
            searchTipsAdapter = new SearchTipsAdapter(getActivity(),alltips);
            searchTipsAdapter.setOnItemClickListener(new IOnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //TODO，点击自动补全获取经纬度信息的回调。
                    hideAnim();
                    AlarmDetail.myAppBarLayout.setExpanded(true);
                    LatLng posLatlng=new LatLng(alltips.get(position).getPoint().getLatitude(),
                            alltips.get(position).getPoint().getLongitude());
                    AlarmDetail.myMap.movetoLatlng(posLatlng,true);
                    searchHistoryDB.insertHistory(alltips.get(position));
                }

                @Override
                public void onItemDeleteClick(int position) {

                }
            });
            rvSearchList.setAdapter(searchTipsAdapter);
            searchTipsAdapter.notifyDataSetChanged();
            deleatText.setVisibility(View.GONE);
        }
    }

    private void hideAnim() {
        KeyBoardUtils.closeKeyboard(getContext(), etSearchKeyword);
        mCircularRevealAnim.hide(clearPic, view);
    }

    private void checkHistorySize() {
        if (historys.size() < 1) {
            searchUnderline.setVisibility(View.GONE);
            deleatText.setVisibility(View.GONE);
        } else {
            searchUnderline.setVisibility(View.VISIBLE);
            deleatText.setVisibility(View.VISIBLE);
        }
    }

    private void setAllHistorys() {
        historys.clear();
        historys.addAll(allHistorys);
        checkHistorySize();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            //滑动触发收起小键盘。
            KeyBoardUtils.closeKeyboard(getActivity(),etSearchKeyword);
        }
        return false;
    }
}
